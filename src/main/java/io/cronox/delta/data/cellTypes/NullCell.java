package io.cronox.delta.data.cellTypes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NullCell implements Cell {

	@Value("${data.null.string:abc}")
	private String nullString;

	public NullCell() { }

	public int compareTo(NullCell o) {
		return 0;
	}
	
	@Override
	public int compareTo(Cell o) {
		if(o instanceof NullCell) return 0;
		
		return this.toString().compareTo(o.toString());
	}
	
	public String toString() {
		return nullString;
	}

	@Override
	public Object getValue() {
		return this;
	}
}
