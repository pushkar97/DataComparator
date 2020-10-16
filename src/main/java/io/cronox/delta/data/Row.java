package io.cronox.delta.data;

import java.util.ArrayList;

import io.cronox.delta.data.cellTypes.Cell;

public class Row extends ArrayList<Cell> implements Comparable<Row> {

	private static final long serialVersionUID = -6135869278820309436L;

	@Override
	public int compareTo(Row o) {
		for(int i = 0; i < this.size(); i++) {
			if(this.get(i).compareTo(o.get(i)) != 0) return this.get(i).compareTo(o.get(i));
		}
		return 0;
	}
}
