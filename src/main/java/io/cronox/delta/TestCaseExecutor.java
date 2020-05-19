package io.cronox.delta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.cronox.delta.comparators.DataSetComparator;
import io.cronox.delta.data.CellFactory;
import io.cronox.delta.dataSetGenerators.DataSetGenerator;
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
		comparer.setSet1(getSourceDataSetGenerator().generate(test.getSourceQuery()));
		comparer.setSet2(getTargetDataSetGenerator().generate(test.getTargetQuery()));
		getSourceDataSetGenerator().subscribe(BeanUtil.getBean(LoadingObserver.class));
		getTargetDataSetGenerator().subscribe(BeanUtil.getBean(LoadingObserver.class));
		comparer.subscribe(BeanUtil.getBean(ProgressObserver.class));
		helper.printInfo("Unique Rows");
		helper.printInfo("Source : "+comparer.getSet1().getDataSet().size());
		helper.printInfo("Target : "+comparer.getSet2().getDataSet().size());
		helper.printInfo("Duplicates");
		helper.printInfo("Source : "+comparer.getSet1().getDuplicates().size());
		helper.printInfo("Target : "+comparer.getSet2().getDuplicates().size());
		comparer.compare();
		helper.printInfo("Matched : "+comparer.getMatched().size());
		helper.printInfo("");
		helper.printInfo("Mismatched");
		helper.printInfo("Source : "+comparer.getSet1().getDataSet().size());
		helper.printInfo("Target : "+comparer.getSet2().getDataSet().size());
		ResultGenerator generator = BeanUtil.getBean(comparer.getResultGenerator());
		DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		generator.generate(comparer, path.concat(test.getId()+LocalDateTime.now().format(f).replace(':', '_')));
		return path;
	}
	
	public CellFactory getFactory() {
		return factory;
	}
	
	public DataSetGenerator  getSourceDataSetGenerator() {
		if(sourceDataSetGenerator == null) {
			sourceDataSetGenerator = test.getSourceConnection().getDataSetGenerator(factory);
		}
		return this.sourceDataSetGenerator;
	}
	
	public DataSetGenerator getTargetDataSetGenerator() {
		if(targetDataSetGenerator == null) {
			targetDataSetGenerator = test.getTargetConnection().getDataSetGenerator(factory);
		}
		return this.targetDataSetGenerator;
	}
}
