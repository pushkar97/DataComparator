package io.cronox.delta.repository;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cronox.delta.connection.DataSourceConnection;
import io.cronox.delta.exceptions.ConnectionAlreadyExistsException;
import io.cronox.delta.exceptions.ConnectionNotFoundException;
import io.cronox.delta.models.Connections;

public class GenericConnectionRepository<T extends DataSourceConnection> implements ConnectionRepository<T, String> {

	Logger logger = LoggerFactory.getLogger(GenericConnectionRepository.class);
	
	private Connections<T> connections = new Connections<T>();

	private JAXBContext jaxbContext = null;
	private Marshaller jaxbMarshaller = null;
	private Unmarshaller jaxbUnmarshaller = null;
	private File connectionXml = null;

	@SuppressWarnings("unchecked")
	public GenericConnectionRepository(File connectionXml, Class<T> type) throws JAXBException {

		jaxbContext = JAXBContext.newInstance(type, connections.getClass());
		jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		this.connectionXml = connectionXml;
		try {
			this.connections = ((Connections<T>) jaxbUnmarshaller.unmarshal(connectionXml));
		}catch(Exception e) {
			logger.info("Connection file not Found or invalid, creating new file. : {}",connectionXml.getAbsolutePath());
		}
	}

	@Override
	public T get(String id) {
		return connections.getConnections().stream().filter(e -> e.getId().equals(id)).findFirst()
				.orElseThrow(() -> new ConnectionNotFoundException(id));
	}

	@Override
	public List<T> list() {
		return connections.getConnections();
	}

	@Override
	public T add(T conn) {
		try {
			this.get(conn.getId());
			throw new ConnectionAlreadyExistsException(conn.getId());
		} catch (ConnectionNotFoundException e) {
			connections.getConnections().add(conn);
		}

		return conn;
	}

	@Override
	public boolean delete(String id) {
		return connections.getConnections().remove(this.get(id));
	}

	@Override
	public T update(T conn) {
		try {
			T connDb = this.get(conn.getId());
			int index = connections.getConnections().indexOf(connDb);
			return connections.getConnections().set(index, conn);
		} catch (ConnectionNotFoundException e) {
			connections.getConnections().add(conn);
		}
		return conn;
	}

	@Override
	public void save() throws JAXBException {
		jaxbMarshaller.marshal(connections, this.connectionXml);
	}

}
