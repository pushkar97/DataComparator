package io.cronox.delta.resultGenerator;

import io.cronox.delta.comparators.DataSetComparator;

public interface ResultGenerator {
	
	public void generate(DataSetComparator comp, String path); 
}
