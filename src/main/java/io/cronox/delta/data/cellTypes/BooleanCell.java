package io.cronox.delta.data.cellTypes;

public class BooleanCell implements Cell {

	private boolean value;
	
	public BooleanCell(boolean value) {
		this.value = value;
	}
	@Override
	public int compareTo(Cell o) {
		if(o instanceof BooleanCell)
			return this.compareTo((BooleanCell)o);
		if(o.getValue() instanceof Number) 
			return Boolean.compare(this.value, ((Number)o.getValue()).doubleValue() != 0);
		
		return this.toString().compareTo(o.toString());
	}
	
	public int compareTo(BooleanCell o) {
		return Boolean.compare(this.value, o.value);
	}

	public Boolean getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Boolean.valueOf(value).toString();
	}
}
