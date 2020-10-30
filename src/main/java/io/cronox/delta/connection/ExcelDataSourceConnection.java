package io.cronox.delta.connection;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import io.cronox.delta.data.CellFactory;
import io.cronox.delta.dataSetGenerators.DataSetGenerator;
import io.cronox.delta.dataSetGenerators.ExcelDataSetGenerator;
import io.cronox.delta.exceptions.ConnectionFailedException;
import io.cronox.delta.helpers.SpreadSheet;
import lombok.Data;

@Data
@XmlType(propOrder = { "url" })
@XmlRootElement(name = "Excel_Connection")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExcelDataSourceConnection implements DataSourceConnection {

	@XmlAttribute(required = true)
	private String id;

	@XmlElement
	private String url;

	@XmlTransient
	private SpreadSheet workbook;

	@XmlTransient
	private PropertyChangeSupport support;
	
	public ExcelDataSourceConnection() {
		support = new PropertyChangeSupport(this);
	}
	
	public void subscribe(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	@Override
	public void setMaxRows(int maxRows) {

	}

	@Override
	public void setFetchSize(int fetchSize) {

	}

	@Override
	public boolean testConnection() {
		return new File(url).exists();
	}

	@Override
	public DataSetGenerator getDataSetGenerator(CellFactory factory) {
		if (!this.testConnection()) {
			throw new ConnectionFailedException(id);
		}
		SpreadSheet wb;
		try {
			wb = getcurrentWorkbook();
		} catch (IOException e) {
			throw new ConnectionFailedException(e.getMessage());
		}
		return new ExcelDataSetGenerator(factory, wb);
	}

	private SpreadSheet getcurrentWorkbook() throws IOException {
		if (this.workbook == null) {
			this.workbook = new SpreadSheet(url);
		}
		return this.workbook;
	}

	public void finalize() {
		try {
			workbook.closeWorkbook();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
