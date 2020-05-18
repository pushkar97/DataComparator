package io.cronox.delta.data.cellTypes;

import java.util.Date;

public class DateCell implements Cell {

	private Date value;
	
	public DateCell(Date value) {
		this.value = value;
	}

	public int compareTo(DateCell o) {
		return this.value.compareTo(o.value);
	}

	@Override
	public int compareTo(Cell o) {
		if(o instanceof DateCell) return this.compareTo((DateCell)o);
		
		return this.toString().compareTo(o.toString());
	}

	public Date getValue() {
		return value;
	}

	public String toString() {
		return value.toString();
	}

}
