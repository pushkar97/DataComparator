package io.cronox.delta.data.cellTypes;

import java.math.BigDecimal;

public class FloatCell implements Cell {

	private float value;

	public FloatCell(float value) {
		this.value = value;
	}

	public int compareTo(FloatCell o) {
		return Float.compare(this.value, o.value);
	}

	@Override
	public int compareTo(Cell o) {
		if (o instanceof FloatCell)
			return this.compareTo((FloatCell) o);

		if (o instanceof BigDecimalCell)
			return BigDecimal.valueOf(((FloatCell) this).value).compareTo(((BigDecimalCell) o).getValue());

		if (o.getValue() instanceof Number)
			return Double.compare(((Number) this.value).doubleValue(), ((Number) o.getValue()).doubleValue());
		
		if (o instanceof BooleanCell)
			return Boolean.compare(((Number) this.value).doubleValue() != 0, (boolean) o.getValue());
		
		return this.toString().compareTo(o.toString());
	}

	public Float getValue() {
		return value;
	}

	public String toString() {
		return Float.valueOf(value).toString();
	}

}
