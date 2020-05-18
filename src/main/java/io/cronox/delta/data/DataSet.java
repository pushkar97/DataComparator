package io.cronox.delta.data;

import java.util.Set;
import java.util.TreeSet;

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
}
