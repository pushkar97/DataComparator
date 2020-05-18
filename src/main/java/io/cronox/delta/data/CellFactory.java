package io.cronox.delta.data;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import io.cronox.delta.data.cellTypes.BooleanCell;
import io.cronox.delta.data.cellTypes.ByteCell;

@Component
public class CellFactory {

	private Map<Boolean, BooleanCell> booleanMap;
	
	private Map<Byte, ByteCell> byteMap;
	
	//private Map<Number, Cell> NumberMap;
	public CellFactory() {
		booleanMap = new TreeMap<Boolean, BooleanCell>();
		byteMap = new TreeMap<Byte, ByteCell>();
		//NumberMap = new TreeMap<Number, Cell> ();
	}

	public BooleanCell getBooleanCell(boolean value) {
		BooleanCell cell = booleanMap.get(value);
		if (cell != null)
			return cell;
		cell = new BooleanCell(value);
		booleanMap.put(value, cell);
		return cell;
	}
	
	public ByteCell getByteCell(byte value) {
		ByteCell cell = byteMap.get(value);
		if (cell != null)
			return cell;
		cell = new ByteCell((byte)value);
		byteMap.put(value, cell);
		return cell;
	}
}
