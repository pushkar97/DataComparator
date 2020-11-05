package io.cronox.delta.data.cellTypes;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class StringCell implements Cell {

	private final String value;
	
	public StringCell(String value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(@NotNull Cell o) {
		if (o instanceof StringCell)
			return this.compareTo((StringCell) o);

		if (o instanceof BigDecimalCell)
			return new BigDecimal(this.value).toPlainString().compareTo(((BigDecimalCell)o).getValue().toPlainString());

		return this.toString().compareTo(o.toString());
	}
	
	public int compareTo(StringCell o) {
		return this.value.compareTo(o.value);
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}

}
