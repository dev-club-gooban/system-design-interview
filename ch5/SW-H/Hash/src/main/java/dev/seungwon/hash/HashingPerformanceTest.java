package dev.seungwon.hash;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.seungwon.hash.consistent.ConsistentHashDatabaseRouter;
import dev.seungwon.hash.general.ModuloHashDatabaseRouter;

public class HashingPerformanceTest {
	public static void main(String[] args) throws SQLException {
		// 초기 설정
		List<DatabaseConnection> initialReplicas = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			initialReplicas.add(new DatabaseConnection("replica-" + i));
		}

		// 라우터 생성
		ModuloHashDatabaseRouter moduloRouter =
			new ModuloHashDatabaseRouter(new ArrayList<>(initialReplicas));
		ConsistentHashDatabaseRouter consistentRouter =
			new ConsistentHashDatabaseRouter(new ArrayList<>(initialReplicas), 10);

		// 10,000개 사용자 ID 생성 및 초기 매핑 저장
		int numUsers = 10000;
		String[] userIds = new String[numUsers];
		Map<String, String> initialModuloRouting = new HashMap<>();
		Map<String, String> initialConsistentRouting = new HashMap<>();

		for (int i = 0; i < numUsers; i++) {
			userIds[i] = "user-" + i;
			String queryId = "select-data";

			// 초기 라우팅 결과 저장
			initialModuloRouting.put(userIds[i],
				moduloRouter.getConnectionForQuery(queryId, userIds[i]).getId());
			initialConsistentRouting.put(userIds[i],
				consistentRouter.getConnectionForQuery(queryId, userIds[i]).getId());
		}

		// 새 레플리카 추가
		DatabaseConnection newReplica = new DatabaseConnection("replica-4");
		moduloRouter.addReplica(newReplica);
		consistentRouter.addReplica(newReplica);

		// 변경 영향 측정
		int moduloChanges = 0;
		int consistentChanges = 0;

		for (String userId : userIds) {
			String queryId = "select-data";

			String currentModuloRoute =
				moduloRouter.getConnectionForQuery(queryId, userId).getId();
			String currentConsistentRoute =
				consistentRouter.getConnectionForQuery(queryId, userId).getId();

			if (!initialModuloRouting.get(userId).equals(currentModuloRoute)) {
				moduloChanges++;
			}

			if (!initialConsistentRouting.get(userId).equals(currentConsistentRoute)) {
				consistentChanges++;
			}
		}

		System.out.println("레플리카 추가 후 라우팅 변경 영향:");
		System.out.println("일반 해시: " + moduloChanges + "/" + numUsers +
			" (" + (moduloChanges * 100.0 / numUsers) + "%)");
		System.out.println("안정 해시: " + consistentChanges + "/" + numUsers +
			" (" + (consistentChanges * 100.0 / numUsers) + "%)");
	}
}
