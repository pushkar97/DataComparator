package io.cronox.delta.data.cellTypes;

import java.math.BigDecimal;

public class DoubleCell implements Cell {

	private double value;

	public DoubleCell(double value) {
		this.value = value;
	}

	public int compareTo(DoubleCell o) {
		return Double.compare(this.value, o.value);
	}

	@Override
	public int compareTo(Cell o) {
		if (o instanceof DoubleCell) return this.compareTo((DoubleCell)o);

		if (o instanceof BigDecimalCell)
			return BigDecimal.valueOf(((DoubleCell) this).value).compareTo(((BigDecimalCell) o).getValue());

		if (o.getValue() instanceof Number)
			return Double.compare(((Number)this.value).doubleValue(),((Number) o.getValue()).doubleValue());
		
		if (o instanceof BooleanCell)
			return Boolean.compare(this.value != 0 ,(boolean) o.getValue());
		
		return this.toString().compareTo(o.toString());
	}

	public Double getValue() {
		return value;
	}

	public String toString() {
		return Double.valueOf(value).toString();
	}
}
