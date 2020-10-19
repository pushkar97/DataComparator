package io.cronox.delta;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.cronox.delta.connection.ExcelDataSourceConnection;
import io.cronox.delta.connection.JDBCDataSourceConnection;
import io.cronox.delta.repository.GenericConnectionRepository;

@Configuration
public class RepositoryConfig {
	
	@Value("${connection.repository.jdbc.uri}")
	private String jdbcConnectionXmlPath;
	
	@Value("${connection.repository.excel.uri}")
	private String excelConnectionXmlPath;
	
	@Bean
	GenericConnectionRepository<JDBCDataSourceConnection> jdbcRepository() {
		try {
			return new GenericConnectionRepository<>
				(new File(jdbcConnectionXmlPath), JDBCDataSourceConnection.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Bean
	GenericConnectionRepository<ExcelDataSourceConnection> excelRepository() {
		try {
			return new GenericConnectionRepository<>
				(new File(excelConnectionXmlPath), ExcelDataSourceConnection.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
}
