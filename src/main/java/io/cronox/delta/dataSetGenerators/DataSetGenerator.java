package io.cronox.delta.dataSetGenerators;

import java.beans.PropertyChangeListener;

import io.cronox.delta.data.DataSet;

public interface DataSetGenerator {

	public DataSet generate(String query);
	
	public void subscribe(PropertyChangeListener pcl);
}
