package io.cronox.delta.helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.opencsv.CSVWriter;

import io.cronox.delta.data.DataSet;
import io.cronox.delta.data.Row;

@Component
public class CsvHelpers {

	Logger logger = LoggerFactory.getLogger(CsvHelpers.class);

	public void csvWriterOneByOne(List<String[]> stringArray, String path) throws Exception {
		CSVWriter writer = new CSVWriter(new FileWriter(path));
		for (String[] array : stringArray) {
			writer.writeNext(array);
		}
		writer.close();
	}

	public void datasetToCsv(DataSet data, File file) {
//		logger.info("Writing CSV data to {}", path);
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(file));
			// set headers
			writer.writeNext(data.getHeader().stream().map(c -> c.toString()).toArray(String[]::new));

			// write data
			for (Row array : data.getDataSet()) {
				writer.writeNext(array.stream().map(c -> c.toString()).toArray(String[]::new));
			}
			if(writer !=  null) writer.close();
		} catch (IOException e) {
			logger.error("Error loading file : {}, Error message : {}",file.getAbsolutePath(), e.getMessage());
			e.printStackTrace();
			
		}
	}
}
