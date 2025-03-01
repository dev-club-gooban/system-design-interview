package dev.seungwon.hash;

import lombok.Getter;

@Getter
public class DatabaseConnection {
	private final String id;

	public DatabaseConnection(String id) {
		this.id = id;
	}
}
