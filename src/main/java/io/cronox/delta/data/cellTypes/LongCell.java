package io.cronox.delta.data.cellTypes;

public class LongCell implements Cell {

	private long value;
	
	public LongCell(long value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(Cell o) {
		
		if (o instanceof LongCell)
			return this.compareTo((LongCell) o);
		
		if (o.getValue() instanceof Number)
			return Double.compare(((Number)this.value).doubleValue(),((Number) o.getValue()).doubleValue());
		
		if (o instanceof BooleanCell)
			return Boolean.compare(((Number)this.value).doubleValue() != 0 ,(boolean) o.getValue());
		
		return this.toString().compareTo(o.toString());
	}

	public int compareTo(LongCell o) {
		return Long.compare(this.value, o.value);
	}
	
	public Long getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Long.valueOf(value).toString();
	}
}
