package io.cronox.delta.commands;

import io.cronox.delta.data.DataSet;
import io.cronox.delta.helpers.shellHelpers.DataSetTableModel;
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
import org.springframework.shell.table.TableBuilder;

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

	Connections connections;

	public DefaultComparator previousExecutionResult;

	public TestExecutionCommands(ShellHelper helper,
								 Connections connections){
		this.helper = helper;
		this.connections = connections;
	}

	@ShellMethod(value = "Run quick test without creating any testsuite or tests", key = {"qt", "quickTest"})
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
		
		DefaultComparator comparator = new DefaultComparator();
		TestCaseExecutor ex = new TestCaseExecutor(tempTest, comparator);
		String path = ex.execute();
		previousExecutionResult = comparator;
		return helper.getSuccessMessage("Test executed Successfully. Find results at : " + path);
	}

	@ShellMethod(value = "view data returned from query")
	public void peak(@ShellOption(value = {"-s", "--select"}, defaultValue = "MATCHED SOURCE_MISMATCH TARGET_MISMATCH") String reportsArg,
					 @ShellOption(value = {"-l", "--limit"}, defaultValue="5") long max,
					 @ShellOption(value = {"-b", "--border"}, defaultValue = "oldschool") BorderStyle borderStyle) {

		var reports = Arrays.stream(reportsArg.split(" ")).map(ReportType::valueOf).collect(Collectors.toCollection(HashSet::new));
		if(reports.contains(ReportType.MATCHED)) {
			var data = previousExecutionResult.getMatched();
			printDataTable(data, ReportType.MATCHED.toString(), max, DatasetExtract.DATA, borderStyle);
		}
		if(reports.contains(ReportType.SOURCE_MISMATCH)) {
			var data = previousExecutionResult.getSet1();
			printDataTable(data, ReportType.SOURCE_MISMATCH.toString(), max, DatasetExtract.DATA, borderStyle);
		}
		if(reports.contains(ReportType.TARGET_MISMATCH)) {
			var data = previousExecutionResult.getSet2();
			printDataTable(data, ReportType.TARGET_MISMATCH.toString(), max, DatasetExtract.DATA, borderStyle);
		}
		if(reports.contains(ReportType.SOURCE_DUPLICATE)) {
			var data = previousExecutionResult.getSet1();
			printDataTable(data, ReportType.TARGET_MISMATCH.toString(), max, DatasetExtract.DUPLICATES, borderStyle);
		}
		if(reports.contains(ReportType.TARGET_DUPLICATE)) {
			var data = previousExecutionResult.getSet2();
			printDataTable(data, ReportType.TARGET_MISMATCH.toString(), max, DatasetExtract.DUPLICATES,borderStyle);
		}
	}

	private void printDataTable(DataSet data, String title, long max, DatasetExtract extract, BorderStyle borderStyle) {
		helper.printInfo("\n\n" + title);
		if (data.size() != 0) {
			DataSetTableModel model = DataSetTableModel.builder(data).limit(max).extract(extract).build();
			var tableBuilder = new TableBuilder(model);
			tableBuilder.addHeaderAndVerticalsBorders(borderStyle);
			helper.print(tableBuilder.build().render(140));
			helper.printInfo("Total rows : " + (model.getRowCount() - 1));
		}else {
			helper.printInfo("No data to display : 0 rows found");
		}
	}
}
