package io.cronox.delta.testExecutors;

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

//Should be used only once per Test
public class TestCaseExecutor {

	private DataSetComparator comparer;
	
	private CellFactory factory;
	
	TestCase test;
	
	private DataSetGenerator sourceDataSetGenerator;
	
	private DataSetGenerator targetDataSetGenerator;
	
	ShellHelper helper;
	String path = "Results/";
	public TestCaseExecutor(TestCase test, DataSetComparator comparer){
		this.comparer = comparer;
		this.factory = new CellFactory();
		this.test = test;
		this.helper = BeanUtil.getBean(ShellHelper.class);
	}
	
	public String execute() {
		getSourceDataSetGenerator().subscribe(BeanUtil.getBean(LoadingObserver.class));
		comparer.setSet1(getSourceDataSetGenerator().generate(test.getSourceQuery()));
		
		getTargetDataSetGenerator().subscribe(BeanUtil.getBean(LoadingObserver.class));
		comparer.setSet2(getTargetDataSetGenerator().generate(test.getTargetQuery()));
		comparer.subscribe(BeanUtil.getBean(ProgressObserver.class));
		helper.print("");
		helper.print("Unique Rows");
		helper.printInfo("\tSource : "+comparer.getSet1().getDataSet().size());
		helper.printInfo("\tTarget : "+comparer.getSet2().getDataSet().size());
		helper.print("");
		try {
			comparer.compare();
		}catch(ComparisonLimitExceededException e) {
			helper.printError(e.getMessage());
		}
		helper.print("");
		helper.printSuccess("Matched : "+comparer.getMatched().size());
		helper.print("");
		helper.print("Mismatched");
		helper.printError("\tSource : "+comparer.getSet1().getDataSet().size());
		helper.printError("\tTarget : "+comparer.getSet2().getDataSet().size());
		helper.print("");
		helper.print("Duplicates");
		helper.printWarning("\tSource : "+comparer.getSet1().getDuplicates().size());
		helper.printWarning("\tTarget : "+comparer.getSet2().getDuplicates().size());
		
		ResultGenerator generator = BeanUtil.getBean(comparer.getResultGenerator());
		DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		return generator.generate(comparer, path.concat(test.getId()+LocalDateTime.now().format(f).replace(':', '_')));
	}
	
	public CellFactory getFactory() {
		return factory;
	}
	
	public DataSetGenerator  getSourceDataSetGenerator() {
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
