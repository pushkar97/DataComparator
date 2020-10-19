package io.cronox.delta.resultGenerator;

import io.cronox.delta.comparators.DataSetComparator;

public interface ResultGenerator {
	
	String generate(DataSetComparator comp, String path) throws InterruptedException;
}
