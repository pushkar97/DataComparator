package io.cronox.delta.data.cellTypes;

import java.math.BigDecimal;

public class BigDecimalCell implements Cell {

	private BigDecimal value;
	
	public BigDecimalCell(BigDecimal value) {
		this.value = value;
	}
	
	public int compareTo(BigDecimalCell o) {
		return this.value.compareTo(o.value);
	}

	@Override
	public int compareTo(Cell o) {
		if(o instanceof BigDecimalCell) return this.compareTo((BigDecimalCell)o);
		
		return this.toString().compareTo(o.toString());
	}

	public BigDecimal getValue() {
		return value;
	}

	public String toString() {
		return value.toString();
	}

}
