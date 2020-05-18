package io.cronox.delta.comparators;

import java.beans.PropertyChangeListener;

import io.cronox.delta.data.DataSet;
import io.cronox.delta.resultGenerator.DefaultComparatorResultGenerator;
import io.cronox.delta.resultGenerator.ResultGenerator;

public interface DataSetComparator {
	
	void compare();
	
	public void setSet1(DataSet set1);

	public void setSet2(DataSet set2);
	
	public DataSet getMatched();

	public DataSet getSet1();

	public DataSet getSet2();
	
	public int getLimit();
	
	public void setLimit(int limit);
	
	public void subscribe(PropertyChangeListener observer);
	
	public <T extends ResultGenerator>Class<DefaultComparatorResultGenerator>  getResultGenerator();
}
