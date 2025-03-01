package dev.seungwon.hash.general;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.seungwon.hash.DatabaseConnection;

public class ModuloHashDatabaseRouter {
	private final List<DatabaseConnection> replicas;
	private final Map<String, Object> queryCache = new ConcurrentHashMap<>();

	public ModuloHashDatabaseRouter(List<DatabaseConnection> replicas) {
		this.replicas = replicas;
	}

	public DatabaseConnection getConnectionForQuery(String queryId, String userId) {
		// 사용자 ID를 기준으로 해시 계산
		int hash = userId.hashCode();
		// 해시값을 레플리카 수로 나눈 나머지로 인덱스 결정
		int index = Math.abs(hash % replicas.size());

		return replicas.get(index);
	}

	// 레플리카 추가
	public void addReplica(DatabaseConnection newReplica) {
		replicas.add(newReplica);
		// 여기서 중요한 점: 레플리카가 추가되면 모듈로 연산의 결과가 완전히 바뀔 수 있음
		// 즉, 기존 캐시가 대부분 유효하지 않게 됨
		queryCache.clear(); // 캐시를 모두 비워야 함
	}

	// 레플리카 제거
	public void removeReplica(DatabaseConnection replica) {
		replicas.remove(replica);
		// 마찬가지로 캐시를 모두 비워야 함
		queryCache.clear();
	}

	public Object getCachedResult(String queryId, String userId) {
		String cacheKey = userId + ":" + queryId;
		return queryCache.get(cacheKey);
	}

	public void cacheResult(String queryId, String userId, Object result) {
		String cacheKey = userId + ":" + queryId;
		queryCache.put(cacheKey, result);
	}
}
