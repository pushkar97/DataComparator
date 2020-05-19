package io.cronox.delta.resultGenerator;

import java.io.File;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.cronox.delta.comparators.DataSetComparator;
import io.cronox.delta.helpers.CsvHelpers;

@Component
public class DefaultComparatorResultGenerator implements ResultGenerator {

	@Autowired
	CsvHelpers csvHelpers;
	
	@Override
	public String generate(DataSetComparator comp, String path) {
		File f = Paths.get(path).toAbsolutePath().toFile();
		if(!f.exists()) {
			f.mkdirs();
		}	
		csvHelpers.datasetToCsv(comp.getMatched(), new File(f,"matched.csv"));
		csvHelpers.datasetToCsv(comp.getSet1(), new File(f,"Source_Mismatch.csv"));
		csvHelpers.datasetToCsv(comp.getSet2(), new File(f,"Target_Mismatch.csv"));
		return f.getAbsolutePath();
	}
}
