package io.cronox.delta.repository;

import java.util.List;

import io.cronox.delta.connection.DataSourceConnection;

public interface ConnectionRepository<T extends DataSourceConnection,S> {
	
	public T get(S id);
	
	public List<T> list();
	
	public T add(T conn);
	
	public boolean delete(S id);
	
	public T update(T conn);
	
	public void save() throws Exception;
}
