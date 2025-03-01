package dev.seungwon.hash.consistent;

import java.sql.SQLException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import dev.seungwon.hash.DatabaseConnection;

public class ConsistentHashDatabaseRouter {
	private final SortedMap<Integer, DatabaseConnection> hashRing = new TreeMap<>();
	private final int numberOfVirtualNodes;

	public ConsistentHashDatabaseRouter(List<DatabaseConnection> replicas, int virtualNodesPerReplica) throws
		SQLException {
		this.numberOfVirtualNodes = virtualNodesPerReplica;

		// 각 레플리카마다 가상 노드 여러 개 생성
		for (DatabaseConnection replica : replicas) {
			addReplicaToRing(replica);
		}
	}

	private void addReplicaToRing(DatabaseConnection replica) throws SQLException {
		for (int i = 0; i < numberOfVirtualNodes; i++) {
			String virtualNodeKey = replica.getId() + "-vnode-" + i;
			int hash = getHash(virtualNodeKey);
			hashRing.put(hash, replica);
		}
	}

	public DatabaseConnection getConnectionForQuery(String queryId, String userId) {
		String routingKey = userId + ":" + queryId;
		int hash = getHash(routingKey);

		// 해시 링에서 키보다 크거나 같은 첫 번째 노드 선택 = 해시링에서 시계 방향으로 가장 가까운 노드
		SortedMap<Integer, DatabaseConnection> tailMap = hashRing.tailMap(hash);
		int nodeHash = tailMap.isEmpty() ? hashRing.firstKey() : tailMap.firstKey();

		return hashRing.get(nodeHash);
	}

	// 레플리카 추가 - 캐시 전체 지우기 필요 없음
	public void addReplica(DatabaseConnection newReplica) throws SQLException {
		addReplicaToRing(newReplica);
		// 일부 키만 영향받으므로 전체 캐시 유지 가능
	}

	private int getHash(String key) {
		// 해시 함수 구현
		final int prime = 16777619;
		int hash = (int)2166136261L;
		for (int i = 0; i < key.length(); i++) {
			hash = (hash ^ key.charAt(i)) * prime;
		}
		return Math.abs(hash);
	}

}
