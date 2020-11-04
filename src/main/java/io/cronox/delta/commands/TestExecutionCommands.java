package io.cronox.delta.commands;

import io.cronox.delta.connection.DataSourceConnection;
import io.cronox.delta.helpers.CliTableHelper;
import io.cronox.delta.models.DatasetExtract;
import io.cronox.delta.models.ReportType;
import lombok.var;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import io.cronox.delta.comparators.DefaultComparator;
import io.cronox.delta.helpers.shellHelpers.ShellHelper;
import io.cronox.delta.models.TestCase;
import io.cronox.delta.repository.Connections;
import io.cronox.delta.testExecutors.TestCaseExecutor;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BorderStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@ShellComponent
public class TestExecutionCommands {

	ShellHelper helper;

	CliTableHelper cliTableHelper;

	Connections connections;

	public TestCaseExecutor previousExecutionResult;

	public TestExecutionCommands(ShellHelper helper,
								 Connections connections,
								 CliTableHelper cliTableHelper){
		this.helper = helper;
		this.connections = connections;
		this.cliTableHelper = cliTableHelper;
	}

	@ShellMethod(value = "Run quick test without creating any testsuite or tests", key = {"qt", "quickTest"})
	public String quickTest(@ShellOption(value = {"-sc", "--sourceConnection"}) String sc,
							@ShellOption(value = {"-sq", "--sourceQuery"}, defaultValue="source.query") String sq,
							@ShellOption(value = {"-tc", "--targetConnection"}) String tc,
							@ShellOption(value = {"-tq", "--targetQuery"}, defaultValue="target.query") String tq,
							@ShellOption(value = {"-ml", "--mismatchLimit"}, defaultValue="0") int limit,
							@ShellOption(value = {"-mr", "--maxRows"}, defaultValue="-1") int maxRows,
							@ShellOption(value = {"-f", "--fetch"}, defaultValue="-1") int fetchSize,
							@ShellOption(value = {"-ne", "--noEvidence"}) boolean noEvidence,
							@ShellOption(value = {"-id", "--testId"}, defaultValue="QuickTest") String testId)
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

		TestCase tempTest = new TestCase(testId);
		if(connections.contains(sc)) {
			DataSourceConnection source_connection = connections.get(sc);
			source_connection.setMaxRows(maxRows);
			source_connection.setFetchSize(fetchSize);
			tempTest.setSourceConnection(source_connection);
		}
		else
			return helper.getErrorMessage("Source connection could not be found.");
		
		if(connections.contains(tc)) {
			DataSourceConnection target_connection = connections.get(tc);
			target_connection.setMaxRows(maxRows);
			target_connection.setFetchSize(fetchSize);
			tempTest.setTargetConnection(target_connection);
		}
		else 
			return helper.getErrorMessage("Target connection could not be found.");

		tempTest.setSourceQuery(sq);
		tempTest.setTargetQuery(tq);
		
		DefaultComparator comparator = new DefaultComparator();
		if(limit != 0)
			comparator.setLimit(limit);
		TestCaseExecutor ex = new TestCaseExecutor(tempTest, comparator);

		ex.execute();
		previousExecutionResult = ex;

		if(!noEvidence) {
			String path = ex.generateEoT();
			return helper.getSuccessMessage("Test executed Successfully. Find results at : " + path);
		}
		return helper.getSuccessMessage("Test executed Successfully.");
	}

	@ShellMethod(value = "Generate Evidence of previously executed test", key = {"evd", "generateEvidence"})
	public String generateEvidence() throws IOException, InterruptedException {
		String path = previousExecutionResult.generateEoT();
		return helper.getSuccessMessage("Find results at : " + path);
	}

	@ShellMethod(value = "peek data of previously executed test")
	public void peek(@ShellOption(value = {"-s", "--select"}, defaultValue = "MATCHED SOURCE_MISMATCH TARGET_MISMATCH") String reportsArg,
					 @ShellOption(value = {"-l", "--limit"}, defaultValue="5") long max,
					 @ShellOption(value = {"-b", "--border"}, defaultValue = "oldschool") BorderStyle borderStyle) {

		var reports = Arrays.stream(reportsArg.split(" ")).map(ReportType::valueOf).collect(Collectors.toCollection(HashSet::new));
		if(reports.contains(ReportType.MATCHED)) {
			var data = previousExecutionResult.getComparator().getMatched();
			cliTableHelper.printDataTable(data, ReportType.MATCHED.toString(), max, DatasetExtract.DATA, borderStyle);
		}
		if(reports.contains(ReportType.SOURCE_MISMATCH)) {
			var data = previousExecutionResult.getComparator().getSet1();
			cliTableHelper.printDataTable(data, ReportType.SOURCE_MISMATCH.toString(), max, DatasetExtract.DATA, borderStyle);
		}
		if(reports.contains(ReportType.TARGET_MISMATCH)) {
			var data = previousExecutionResult.getComparator().getSet2();
			cliTableHelper.printDataTable(data, ReportType.TARGET_MISMATCH.toString(), max, DatasetExtract.DATA, borderStyle);
		}
		if(reports.contains(ReportType.SOURCE_DUPLICATE)) {
			var data = previousExecutionResult.getComparator().getSet1();
			cliTableHelper.printDataTable(data, ReportType.TARGET_MISMATCH.toString(), max, DatasetExtract.DUPLICATES, borderStyle);
		}
		if(reports.contains(ReportType.TARGET_DUPLICATE)) {
			var data = previousExecutionResult.getComparator().getSet2();
			cliTableHelper.printDataTable(data, ReportType.TARGET_MISMATCH.toString(), max, DatasetExtract.DUPLICATES,borderStyle);
		}
	}

}
