package io.cronox.delta.dataSetGenerators;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import io.cronox.delta.data.CellFactory;
import io.cronox.delta.data.DataSet;
import io.cronox.delta.data.Row;
import io.cronox.delta.data.cellTypes.DateCell;
import io.cronox.delta.data.cellTypes.DoubleCell;
import io.cronox.delta.data.cellTypes.StringCell;
import io.cronox.delta.helpers.SpreadSheet;

public class ExcelDataSetGenerator implements DataSetGenerator {

	CellFactory builder;
	
	private final SpreadSheet workbook;
	
	Timer timer;
	
	private final PropertyChangeSupport support;
	
	int rowsDone, rowsDoneOld;
	
	public ExcelDataSetGenerator(CellFactory builder, SpreadSheet workbook){
		this.builder = builder;
		this.workbook = workbook;
		this.support = new PropertyChangeSupport(this);
		this.timer = new Timer("UpdateProgressTimer");
	}

	@Override
	public void subscribe(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}
	
	public void updateProgress() {
		support.firePropertyChange("rowsDone", rowsDoneOld, rowsDone);
		rowsDoneOld = rowsDone;
	}
	
	@Override
	public DataSet generate(String query) {
		DataSet data = new DataSet();
		List<Collection<Object>> dbData;

		dbData = workbook.getData(query);
		Row headers = new Row();
		headers.addAll(dbData.get(0).stream().map(c -> new StringCell(c.toString())).collect(Collectors.toList()));
		data.setHeader(headers);
		timer.schedule(new UpdateProgressTask(), 0, 200);
		dbData.stream().skip(1).forEach(r -> {
			Row row = new Row();
			r.forEach(c -> {
				if(c == null) {
					row.add(builder.getNullCell());
				}
				if (c instanceof Double) {
					row.add(new DoubleCell((double) c));
				}
				if (c instanceof Boolean) {
					//row.add(new BooleanCell((boolean) c));
					row.add(builder.getBooleanCell((boolean) c));
				}
				if (c instanceof String) {
					row.add(new StringCell(c.toString()));
				}
				if (c instanceof Date) {
					row.add(new DateCell((Date) c));
				}
			});
			data.add(row);
			rowsDone++;
		});
		updateProgress();
		return data;
	}

	public class UpdateProgressTask extends TimerTask {
		
		@Override
		public void run() {
			updateProgress();
		}	
	}
}
