package io.cronox.delta.comparators;

import java.beans.PropertyChangeListener;

import io.cronox.delta.data.DataSet;
import io.cronox.delta.exceptions.ComparisonLimitExceededException;
import io.cronox.delta.resultGenerator.DefaultComparatorResultGenerator;
import io.cronox.delta.resultGenerator.ResultGenerator;

public interface DataSetComparator {
	
	void compare() throws ComparisonLimitExceededException;
	
	void setSet1(DataSet set1);

	void setSet2(DataSet set2);
	
	DataSet getMatched();

	DataSet getSet1();

	DataSet getSet2();
	
	int getLimit();
	
	void setLimit(int limit);
	
	void subscribe(PropertyChangeListener observer);
	
	<T extends ResultGenerator> Class<DefaultComparatorResultGenerator>  getResultGenerator();
}
