package io.cronox.delta.comparators;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cronox.delta.data.DataSet;
import io.cronox.delta.data.Row;
import io.cronox.delta.exceptions.ComparisonException;
import io.cronox.delta.exceptions.ComparisonLimitExceededException;
import io.cronox.delta.resultGenerator.DefaultComparatorResultGenerator;

public class DefaultComparator implements DataSetComparator {

	Logger logger = LoggerFactory.getLogger(DefaultComparator.class);

	private PropertyChangeSupport support;
	
	private DataSet set1, set2, matched;
	
	int limit;
	
	private int rowsComplete;
	private int percentComplete;
	
	public DefaultComparator(DataSet set1, DataSet set2, int limit) {
		this(set1, set2);
		this.limit = limit;
	}

	public DefaultComparator(DataSet set1, DataSet set2) {
		this();
		this.set1 = set1;
		this.set2 = set2;
	}
	
	public DefaultComparator() {
		this.support = new PropertyChangeSupport(this);
	}
	
	public void subscribe(PropertyChangeListener observer) {
		support.addPropertyChangeListener(observer);
	}
	
	@Override
	public void compare() throws ComparisonLimitExceededException {
		// check if empty
		if (set1.getDataSet().size() == 0)
			throw new ComparisonException("Set 1 is Empty");
		if (set2.getDataSet().size() == 0)
			throw new ComparisonException("Set 2 is Empty");

		// check column count
		if (set1.getHeader().size() != set2.getHeader().size())
			throw new ComparisonException(
					"Column count did not match, Make sure number of columns in both datasets are same");

		Iterator<Row> set1I = set1.getDataSet().iterator();
		Iterator<Row> set2I = set2.getDataSet().iterator();
		
		int set1MismatchCount = 0;
		int set2MismatchCount = 0;
		
		matched = new DataSet();
		matched.setHeader(set1.getHeader());

		Row set1R = set1I.next();
		Row set2R = set2I.next();

		while (true) {
			int result = set1R.compareTo(set2R);
			if (result == 0) {
				matched.add(set1R);
				set1I.remove();
				set2I.remove();
				rowsComplete+=2;
				if (!set1I.hasNext())
					break;
				if (!set2I.hasNext())
					break;
				set1R = set1I.next();
				set2R = set2I.next();
			}

			if (result < 0) {
				rowsComplete++;
				if (!set1I.hasNext())
					break;
				set1R = set1I.next();
				
				if(limit > 0 && ++set1MismatchCount >= limit)
					throw new ComparisonLimitExceededException(
							"Too Many mismatches in source : "+ set1MismatchCount);
			}

			if (result > 0) {
				rowsComplete++;
				if (!set2I.hasNext())
					break;
				set2R = set2I.next();
				if(limit > 0 && ++set2MismatchCount >= limit)
					throw new ComparisonLimitExceededException(
							"Too Many mismatches in target : "+ set2MismatchCount);
			}
			updateProgress();
		}
		rowsComplete+=set1.size();
		rowsComplete+=set2.size();
		updateProgress();
	}

	public void updateProgress() {
		int val = ((rowsComplete * 100) / (set1.size() + set2.size() + (matched.size() * 2))) ;
		this.support.firePropertyChange("percentComplete", this.percentComplete, val);
		this.percentComplete = val;
	}
	
	public DataSet getMatched() {
		return matched;
	}

	public DataSet getSet1() {
		return set1;
	}

	public DataSet getSet2() {
		return set2;
	}

	public void setSet1(DataSet set1) {
		this.set1 = set1;
	}

	public void setSet2(DataSet set2) {
		this.set2 = set2;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public  Class<DefaultComparatorResultGenerator> getResultGenerator() {
		return DefaultComparatorResultGenerator.class;
	}
	
	
}
