package io.cronox.delta.commands;

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

@ShellComponent
public class TestExecutionCommands {

	@Autowired
	ShellHelper helper;
	
	@Autowired
    InputReader inputReader;
	
	@Autowired
	Connections connections;
	
	@Autowired
	ProgressObserver progressObserver;
	
	@Autowired
	LoadingObserver loadingObserver;
	
	@ShellMethod(value = "Run quik test without creating any testsuits or tests")
	public String quickTest(String sc, String sq, String tc, String tq) {
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
		return helper.getSuccessMessage("Test executed Successfully. Find results at :"+path);
	}
}
