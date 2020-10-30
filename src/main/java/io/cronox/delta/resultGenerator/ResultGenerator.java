package io.cronox.delta.resultGenerator;

import io.cronox.delta.comparators.DataSetComparator;
import io.cronox.delta.models.TestCase;

import java.io.IOException;

public interface ResultGenerator {
	
	String generate(DataSetComparator comp, TestCase test, String path) throws InterruptedException, IOException;
}
