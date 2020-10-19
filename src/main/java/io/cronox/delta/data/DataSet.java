package io.cronox.delta.data;

import io.cronox.delta.models.DatasetExtract;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DataSet {

	private Set<Row> dataSet = new TreeSet<Row>();
	
	private Set<Row> duplicates = new TreeSet<Row>();
	
	private Row Header = new Row();
	
	public void add(Row row) {
		if(!dataSet.add(row)) duplicates.add(row);
	}

	public Row getHeader() {
		return Header;
	}

	public void setHeader(Row header) {
		Header = header;
	}

	public Set<Row> getDataSet() {
		return dataSet;
	}

	public Set<Row> getDuplicates() {
		return duplicates;
	}
	
	public int size() {
		return dataSet.size();
	}

	public Set<Row> getData(DatasetExtract extract){
		switch (extract){
			case DATA:
				return getDataSet();
			case DUPLICATES:
				return getDuplicates();
			default:
				throw new RuntimeException("Invalid value for Extract");
		}
	}
}
