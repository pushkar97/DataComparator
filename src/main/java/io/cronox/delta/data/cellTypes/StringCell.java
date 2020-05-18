package io.cronox.delta.data.cellTypes;

public class StringCell implements Cell {

	private String value;
	
	public StringCell(String value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(Cell o) {
		if (o instanceof StringCell)
			return this.compareTo((StringCell) o);

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
