package io.cronox.delta.connection;

import java.beans.PropertyChangeListener;

import io.cronox.delta.data.CellFactory;
import io.cronox.delta.dataSetGenerators.DataSetGenerator;

public interface DataSourceConnection {
	
	String getId();
	
	String getUrl();
	
	boolean testConnection();

	DataSetGenerator getDataSetGenerator(CellFactory factory);
	
	void subscribe(PropertyChangeListener pcl);
}
