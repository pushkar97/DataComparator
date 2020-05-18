package io.cronox.delta.data.cellTypes;

public class ShortCell implements Cell {

	private short value;

	@Override
	public int compareTo(Cell o) {
		if (o instanceof ShortCell)
			return this.compareTo((ShortCell) o);

		if (o.getValue() instanceof Number)
			return Double.compare(((Number)this.value).doubleValue(),((Number) o.getValue()).doubleValue());
		
		if (o instanceof BooleanCell)
			return Boolean.compare(((Number)this.value).doubleValue() != 0 ,(boolean) o.getValue());
		
		return this.toString().compareTo(o.toString());
	}

	public int compareTo(ShortCell o) {
		return Short.compare(this.value, o.value);
	}
	
	public ShortCell(short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Short.valueOf(value).toString();
	}

}
