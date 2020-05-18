package io.cronox.delta.data.cellTypes;

public class IntegerCell implements Cell {

	private int value;
	
	public int compareTo(IntegerCell o) {
		return Integer.compare(this.value, o.value);
	}

	public int compareTo(Cell o) {
		if(o instanceof IntegerCell) return this.compareTo((IntegerCell)o);
		
		if (o.getValue() instanceof Number)
			return Double.compare(((Number)this.value).doubleValue(),((Number) o.getValue()).doubleValue());
		
		if (o instanceof BooleanCell)
			return Boolean.compare(((Number)this.value).doubleValue() != 0 ,(boolean) o.getValue());
		
		
		return this.toString().compareTo(o.toString());
	}
	
	public IntegerCell(int value){
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}

	public String toString() {
		return Integer.valueOf(value).toString();
	}
}
