package io.cronox.delta.testExecutors;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.cronox.delta.comparators.DataSetComparator;
import io.cronox.delta.data.CellFactory;
import io.cronox.delta.dataSetGenerators.DataSetGenerator;
import io.cronox.delta.exceptions.ComparisonLimitExceededException;
import io.cronox.delta.helpers.BeanUtil;
import io.cronox.delta.helpers.shellHelpers.ShellHelper;
import io.cronox.delta.models.TestCase;
import io.cronox.delta.observers.LoadingObserver;
import io.cronox.delta.observers.ProgressObserver;
import io.cronox.delta.resultGenerator.ResultGenerator;
import lombok.var;

//Should be used only once per Test
public class TestCaseExecutor {

	private final DataSetComparator comparator;
	
	private final CellFactory factory;
	
	TestCase test;
	
	private DataSetGenerator sourceDataSetGenerator;
	
	private DataSetGenerator targetDataSetGenerator;
	
	ShellHelper helper;
	String path = "Results/";
	public TestCaseExecutor(TestCase test, DataSetComparator comparator){
		this.comparator = comparator;
		this.factory = BeanUtil.getBean(CellFactory.class);
		this.test = test;
		this.helper = BeanUtil.getBean(ShellHelper.class);
	}
	
	public String execute() throws InterruptedException {
		var execution_start = Instant.now();

		getSourceDataSetGenerator().subscribe(BeanUtil.getBean(LoadingObserver.class));
		var source_read_start = Instant.now();
		comparator.setSet1(getSourceDataSetGenerator().generate(test.getSourceQuery()));
		var source_read_timeElapsed = Duration.between(source_read_start, Instant.now());


		getTargetDataSetGenerator().subscribe(BeanUtil.getBean(LoadingObserver.class));
		var target_read_start = Instant.now();
		comparator.setSet2(getTargetDataSetGenerator().generate(test.getTargetQuery()));
		var target_read_timeElapsed = Duration.between(target_read_start, Instant.now());


		comparator.subscribe(BeanUtil.getBean(ProgressObserver.class));
		helper.print("\nUnique Rows");
		helper.printInfo("Source : "+ comparator.getSet1().getDataSet().size()
				+ "\nTarget : "+ comparator.getSet2().getDataSet().size() + "\n");

		var comparison_start = Instant.now();
		try {
			comparator.compare();
		}catch(ComparisonLimitExceededException e) {
			helper.printError(e.getMessage());
		}
		var comparison_timeElapsed = Duration.between(comparison_start, Instant.now());

		helper.printSuccess("\nMatched : "+ comparator.getMatched().size());

		helper.print("\nMismatched");
		helper.printError("Source : "+ comparator.getSet1().getDataSet().size()
				+ "\nTarget : "+ comparator.getSet2().getDataSet().size());

		helper.print("\nDuplicates");
		helper.printWarning("Source : "+ comparator.getSet1().getDuplicates().size()
				+ "\nTarget : "+ comparator.getSet2().getDuplicates().size());

		
		ResultGenerator generator = BeanUtil.getBean(comparator.getResultGenerator());
		DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		var eot_gen_start = Instant.now();
		String eot = generator.generate(comparator, path.concat(test.getId()+LocalDateTime.now().format(f).replace(':', '_')));
		var eot_gen_timeElapsed = Duration.between(eot_gen_start, Instant.now());

		helper.printInfo("\nExecution info");
		helper.print("Reading source data took " + helper.getInfoMessage(Long.toString(source_read_timeElapsed.toMillis())) + " milliseconds");
		helper.print("Reading target data took " + helper.getInfoMessage(Long.toString(target_read_timeElapsed.toMillis())) + " milliseconds");
		helper.print("Comparison took " + helper.getInfoMessage(Long.toString(comparison_timeElapsed.toMillis())) + " milliseconds");
		helper.print("Evidence of test generation took " + helper.getInfoMessage(Long.toString(eot_gen_timeElapsed.toMillis())) + " milliseconds");
		helper.print("Total execution took " + helper.getInfoMessage(Long.toString(Duration.between(execution_start, Instant.now()).toMillis())) + " milliseconds");

		return eot;
	}
	
	public CellFactory getFactory() {
		return factory;
	}
	
	public DataSetGenerator getSourceDataSetGenerator() {
		if(this.sourceDataSetGenerator == null) {
			this.sourceDataSetGenerator = test.getSourceConnection().getDataSetGenerator(factory);
		}
		return this.sourceDataSetGenerator;
	}
	
	public DataSetGenerator getTargetDataSetGenerator() {
		if(this.targetDataSetGenerator == null) {
			this.targetDataSetGenerator = test.getTargetConnection().getDataSetGenerator(factory);
		}
		return this.targetDataSetGenerator;
	}
}
