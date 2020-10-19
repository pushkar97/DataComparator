package io.cronox.delta.repository;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.cronox.delta.connection.DataSourceConnection;
import io.cronox.delta.connection.ExcelDataSourceConnection;
import io.cronox.delta.connection.JDBCDataSourceConnection;
import io.cronox.delta.exceptions.ConnectionAlreadyExistsException;
import io.cronox.delta.exceptions.ConnectionNotFoundException;

@Repository
public class Connections {

	private final Map<String, DataSourceConnection> connections = new HashMap<>();

	private final GenericConnectionRepository<JDBCDataSourceConnection> jdbcRepository;

	private final GenericConnectionRepository<ExcelDataSourceConnection> excelRepository;

	@Autowired
	Connections(GenericConnectionRepository<JDBCDataSourceConnection> jdbcRepository,
			GenericConnectionRepository<ExcelDataSourceConnection> excelRepository) {
		this.jdbcRepository = jdbcRepository;
		this.excelRepository = excelRepository;
	}

	@PostConstruct
	public void init() {
		jdbcRepository.list().forEach(c -> connections.put(c.getId(), c));
		excelRepository.list().forEach(c -> {
			if (connections.put(c.getId(), c) != null)
				throw new ConnectionAlreadyExistsException(c.getId());
		});
	}

	public boolean contains(String id) {
		return connections.containsKey(id);
	}

	public DataSourceConnection get(String id) {
		DataSourceConnection conn = connections.get(id);
		if (conn == null)
			throw new ConnectionNotFoundException(id);
		return conn;
	}

	public Map<String, DataSourceConnection> list() {
		return this.connections;
	}

	public DataSourceConnection add(DataSourceConnection conn) {
		if (connections.containsKey(conn.getId())) {
			throw new ConnectionAlreadyExistsException(conn.getId());
		}
		if (conn instanceof JDBCDataSourceConnection) {
			JDBCDataSourceConnection conn1 = jdbcRepository.add((JDBCDataSourceConnection) conn);
			connections.put(conn1.getId(), conn1);
			return conn1;
		}
		if (conn instanceof ExcelDataSourceConnection) {
			ExcelDataSourceConnection conn1 = excelRepository.add((ExcelDataSourceConnection) conn);
			connections.put(conn1.getId(), conn1);
			return conn1;
		}
		return null;
	}

	public void delete(String id) {
		DataSourceConnection conn = connections.get(id);
		if (conn == null)
			throw new ConnectionNotFoundException(id);
		if (conn instanceof JDBCDataSourceConnection) {
			jdbcRepository.delete(id);
		}
		if (conn instanceof ExcelDataSourceConnection) {
			excelRepository.delete(id);
		}
		connections.remove(id);
	}

	public DataSourceConnection update(DataSourceConnection conn) {
		if (!connections.containsKey(conn.getId())) {
			this.add(conn);
		}
		if (!conn.getClass().equals(connections.get(conn.getId()).getClass())) {
			throw new RuntimeException("Invalid class: class cannot be updated for connection");
		}
		if (conn instanceof JDBCDataSourceConnection) {
			return jdbcRepository.update((JDBCDataSourceConnection) conn);
		}
		if (conn instanceof ExcelDataSourceConnection) {
			return excelRepository.update((ExcelDataSourceConnection) conn);
		}
		return null;
	}

	public void save() throws JAXBException {
		jdbcRepository.save();
		excelRepository.save();
	}
}
