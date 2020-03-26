package com.aminadav.database;

import java.util.Vector;

@SuppressWarnings("serial")
public class Column extends Vector<Object> {
	public final String HEADER;

	public Column(String header) {
		super();
		HEADER = header;
	}
}