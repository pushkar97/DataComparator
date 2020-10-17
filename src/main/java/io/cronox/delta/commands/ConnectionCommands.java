package io.cronox.delta.commands;

import java.util.Arrays;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import io.cronox.delta.connection.DataSourceConnection;
import io.cronox.delta.connection.ExcelDataSourceConnection;
import io.cronox.delta.connection.JDBCDataSourceConnection;
import io.cronox.delta.helpers.shellHelpers.InputReader;
import io.cronox.delta.helpers.shellHelpers.ShellHelper;
import io.cronox.delta.repository.Connections;

@ShellComponent
public class ConnectionCommands {

	@Autowired
	ShellHelper helper;
	
	@Autowired
    InputReader inputReader;
	
	@Autowired
	Connections connections;
	
	@ShellMethod(value = "Add new JDBC connection to connections list")
	public String addJdbc(String id) {
		if(connections.contains(id)) 
			return helper.getErrorMessage("Connection with specified id already exists, Choose new id or update existing connection");
		JDBCDataSourceConnection conn = new JDBCDataSourceConnection();
		conn.setId(id);
		do {
            String className = inputReader.prompt("Class Name");
            if (StringUtils.hasText(className)) {
            	conn.setDriverClass(className);
            } else {
                helper.printWarning("Class name CAN NOT be empty string? Please enter valid value!");                
            }
        } while (conn.getDriverClass() == null);
		
		do {
            String url = inputReader.prompt("Connection url");
            if (StringUtils.hasText(url)) {
            	conn.setUrl(url);
            } else {
                helper.printWarning("Connection url CAN NOT be empty string? Please enter valid value!");                
            }
        } while (conn.getUrl() == null);
		
		conn.setUsername(inputReader.prompt("Username",""));
		conn.setPassword(inputReader.prompt("Password","",false));
		
		helper.printInfo("Testing Connection...");
		if(!conn.testConnection()) {
			return helper.getErrorMessage("Connection Test failed! Make sure entered data is correct and server is running.");
		}
		helper.printSuccess("Connection test successful");
		connections.add(conn);
		try {
			connections.save();
		} catch (JAXBException e) {
			helper.printError("Unknown error occurred while saving connection. please refer below stacktrace");
			e.printStackTrace();
		}
		return helper.getSuccessMessage(String.format("Connection %s Successfully Saved", id));
	}
	
	@ShellMethod(value = "Add new Excel connection to connections list")
	public String addExcel(String id) {
		if(connections.contains(id)) 
			return helper.getErrorMessage("Connection with specified id already exists, Choose new id or update existing connection");
	
		ExcelDataSourceConnection conn = new ExcelDataSourceConnection();
		conn.setId(id);
		
		do {
            String url = inputReader.prompt("Connection url");
            if (StringUtils.hasText(url)) {
            	conn.setUrl(url);
            } else {
                helper.printWarning("Connection url CAN NOT be empty string? Please enter valid value!");                
            }
        } while (conn.getUrl() == null);
		
		helper.printInfo("Testing Connection...");
		if(!conn.testConnection()) {
			return helper.getErrorMessage("Connection Test failed! Make sure entered data is correct and File is present.");
		}
		helper.printSuccess("Connection test successful");
		connections.add(conn);
		try {
			connections.save();
		} catch (JAXBException e) {
			helper.printError("Unknown error occurred while saving connection. please refer below stacktrace");
			e.printStackTrace();
		}
		return helper.getSuccessMessage(String.format("Connection %s Successfully Saved", id));
	}

	@ShellMethod(value = "Lists all available connections")
	public void list() {
		Map<String, DataSourceConnection> conns = connections.list();
		if(conns.size() == 0)
			helper.printInfo("No connections available. You can add Connections with 'add-jdbc' and 'add-excel' commands");
		else {
			conns.keySet().forEach(s ->{
				helper.print(s);
			});
		}
	}

	@ShellMethod(value = "Displays Specified Connection information")
	public void get(String id) {
		if(!connections.contains(id))
			helper.printError("Connection does not exist. ");
		DataSourceConnection conn = connections.get(id);
		helper.printInfo(conn.getUrl());
	}
	
	@ShellMethod(value = "Deletes specified connection")
	public String delete(String id){
		if(!connections.contains(id))
			return helper.getErrorMessage("Connection does not exist. ");
		String response = inputReader
				.promptWithOptions("Are you sure, You want to delete connection "+id+ "?", "N", Arrays.asList("Y","N"));
		if(response.equals("Y")) {
			connections.delete(id);
			return helper.getSuccessMessage("Connection deleted successfully.");
		}
		return null;
	}
}
