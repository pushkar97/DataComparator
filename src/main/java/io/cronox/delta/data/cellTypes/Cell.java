package io.cronox.delta.data.cellTypes;

public interface Cell extends Comparable<Cell> {

	String toString();
	
	Object getValue();

}
