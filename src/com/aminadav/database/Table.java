package com.aminadav.database;

import java.util.Vector;

@SuppressWarnings("serial")
public class Table extends Vector<Column> {
	public final String NAME;

	public Table(String name) {
		super();
		NAME = name;
	}
}