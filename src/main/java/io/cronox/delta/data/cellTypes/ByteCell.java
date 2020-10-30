package io.cronox.delta.data.cellTypes;

import java.math.BigDecimal;

public class ByteCell implements Cell {

	private byte value;

	@Override
	public int compareTo(Cell o) {
		if (o instanceof ByteCell)
			return this.compareTo((ByteCell) o);

		if (o instanceof BigDecimalCell)
			return BigDecimal.valueOf(((ByteCell) this).value).compareTo(((BigDecimalCell) o).getValue());

		if (o.getValue() instanceof Number)
			return Double.compare(((Number)this.value).doubleValue(),((Number) o.getValue()).doubleValue());

		if (o instanceof BooleanCell)
			return Boolean.compare(((Number)this.value).doubleValue() != 0 ,(boolean) o.getValue());
		
		return this.toString().compareTo(o.toString());
	}

	public int compareTo(ByteCell o) {
		return Byte.compare(this.value, o.value);
	}

	public ByteCell(byte value) {
		this.value = value;
	}

	public Byte getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Byte.valueOf(value).toString();
	}

}
