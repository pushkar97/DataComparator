package io.cronox.delta.commands;

import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import io.cronox.delta.comparators.DefaultComparator;
import io.cronox.delta.helpers.shellHelpers.InputReader;
import io.cronox.delta.helpers.shellHelpers.ShellHelper;
import io.cronox.delta.models.TestCase;
import io.cronox.delta.observers.LoadingObserver;
import io.cronox.delta.observers.ProgressObserver;
import io.cronox.delta.repository.Connections;
import io.cronox.delta.testExecutors.TestCaseExecutor;
import org.springframework.shell.standard.ShellOption;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ShellComponent
public class TestExecutionCommands {

	ShellHelper helper;

	Connections connections;

	public TestExecutionCommands(ShellHelper helper,
								 Connections connections){
		this.helper = helper;
		this.connections = connections;
	}

	@ShellMethod(value = "Run quick test without creating any testsuite or tests")
	public String quickTest(@ShellOption(value = {"-sc", "--sourceConnection"}) String sc,
							@ShellOption(value = {"-sq", "--sourceQuery"}, defaultValue="source.query") String sq,
							@ShellOption(value = {"-tc", "--targetConnection"}) String tc,
							@ShellOption(value = {"-tq", "--targetQuery"}, defaultValue="target.query") String tq)
			throws IOException, InterruptedException {

		var sourceFile = new File(sq);
		var targetFile = new File(tq);
		if(!sourceFile.exists()){
			if(sq.equals("source.query"))
				return helper.getErrorMessage("Please provide parameter for source query {\"-sq\", \"--sourceQuery\"} or create file named 'source.query' to use for query, if you wish to not pass the parameter");
			helper.printWarning("Provided file path for source query is not valid, parsing as a query...");
		}else {
			helper.printInfo("Reading source query string from " + sourceFile.getAbsolutePath());
			sq = new String(Files.readAllBytes(Paths.get(sourceFile.getAbsolutePath())));
		}
		if(!targetFile.exists()){
			if(sq.equals("target.query"))
				return helper.getErrorMessage("Please provide parameter for target query {\"-tq\", \"--targetQuery\"} or create file named 'target.query' to use for query, if you wish to not pass the parameter");
			helper.printWarning("Provided file path for target query is not valid, parsing as a query...");
		}else{
			helper.printInfo("Reading target query string from " + targetFile.getAbsolutePath());
			tq = new String(Files.readAllBytes(Paths.get(targetFile.getAbsolutePath())));
		}

		TestCase tempTest = new TestCase("QuickTest");
		if(connections.contains(sc))
			tempTest.setSourceConnection(connections.get(sc));
		else
			return helper.getErrorMessage("Source connection could not be found.");
		
		if(connections.contains(tc))
			tempTest.setTargetConnection(connections.get(tc));
		else 
			return helper.getErrorMessage("Target connection could not be found.");

		tempTest.setSourceQuery(sq);
		tempTest.setTargetQuery(tq);
		
		DefaultComparator comp = new DefaultComparator();
		TestCaseExecutor ex = new TestCaseExecutor(tempTest, comp);
		String path = ex.execute();
		return helper.getSuccessMessage("Test executed Successfully. Find results at : " + path);
	}
}
