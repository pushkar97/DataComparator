package io.cronox.delta.data.cellTypes;

public class NullCell implements Cell {

	private static NullCell value = new NullCell();
	
	private NullCell() {}
	
	public int compareTo(NullCell o) {
		return 0;
	}
	
	@Override
	public int compareTo(Cell o) {
		if(o instanceof NullCell) return 0;
		
		return this.toString().compareTo(o.toString());
	}

	public static NullCell getCell() {
		// TODO Auto-generated method stub
		return value;
	}
	
	public String toString() {
		return "{null}";
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return value;
	}

}
