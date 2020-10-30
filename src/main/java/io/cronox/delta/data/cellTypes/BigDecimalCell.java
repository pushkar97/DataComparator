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

		if (o instanceof DoubleCell)
			return this.value.compareTo(BigDecimal.valueOf(((DoubleCell) o).getValue()));

		if (o instanceof FloatCell)
			return this.value.compareTo(BigDecimal.valueOf(((FloatCell) o).getValue()));

		if (o instanceof LongCell)
			return this.value.compareTo(BigDecimal.valueOf(((LongCell) o).getValue()));

		if (o instanceof IntegerCell)
			return this.value.compareTo(BigDecimal.valueOf(((IntegerCell) o).getValue()));

		if (o instanceof ShortCell)
			return this.value.compareTo(BigDecimal.valueOf(((ShortCell) o).getValue()));

		if (o instanceof ByteCell)
			return this.value.compareTo(BigDecimal.valueOf(((ByteCell) o).getValue()));

		return this.toString().compareTo(o.toString());
	}

	public BigDecimal getValue() {
		return value;
	}

	public String toString() {
		return value.toString();
	}

}
