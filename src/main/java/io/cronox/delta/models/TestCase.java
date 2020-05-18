package io.cronox.delta.models;

import io.cronox.delta.connection.DataSourceConnection;
import lombok.Data;

@Data
public class TestCase {

	private String id;

	private DataSourceConnection sourceConnection;

	private String sourceQuery;

	private DataSourceConnection targetConnection;

	private String targetQuery;
	
	public TestCase(String id) {
		this.id = id;
	}
}
