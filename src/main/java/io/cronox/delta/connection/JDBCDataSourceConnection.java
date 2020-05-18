package io.cronox.delta.connection;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import io.cronox.delta.data.CellFactory;
import io.cronox.delta.dataSetGenerators.DataSetGenerator;
import io.cronox.delta.dataSetGenerators.JDBCDataSetGenerator;
import io.cronox.delta.exceptions.ConnectionFailedException;
import lombok.Data;

@Data
@XmlType(propOrder = { "url", "driverClass", "username", "password" })
@XmlRootElement(name = "JDBC_Connection")
@XmlAccessorType(XmlAccessType.FIELD)
public class JDBCDataSourceConnection implements DataSourceConnection {

	@XmlAttribute(required = true)
	private String id = "";

	@XmlElement
	private String url;

	@XmlElement
	private String driverClass;

	@XmlElement
	private String username;

	@XmlElement
	private String password;

	@XmlTransient
	private DriverManagerDataSource dataSource;
	
	@XmlTransient
	private JdbcTemplate template;
	
	@XmlTransient
	private PropertyChangeSupport support;
	
	public JDBCDataSourceConnection(){
		support = new PropertyChangeSupport(this);
	}
	
	public void subscribe(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}
	
	@Override
	public DataSetGenerator getDataSetGenerator(CellFactory factory) {
		if(!this.testConnection()) {
			throw new ConnectionFailedException(id);
		}
		return new JDBCDataSetGenerator(factory, getJdbcTemplate());
	}
	
	private DriverManagerDataSource getDataSource() {
		if(dataSource == null) {
			dataSource = new DriverManagerDataSource();
			dataSource.setUrl(url);
			dataSource.setDriverClassName(driverClass);
			dataSource.setUsername(username);
			dataSource.setPassword(password);
		}
		return dataSource;
	}

	private JdbcTemplate getJdbcTemplate() {
		if(template == null) {
			this.template =  new JdbcTemplate(getDataSource());
		}
		return template;
	}
	@Override
	public boolean testConnection() {
		try {
			this.getDataSource().getConnection().close();
			return true;
		}catch(SQLException e) {
			return false;
		}
	}
}
