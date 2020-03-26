package com.aminadav.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

interface DB {
	public boolean exist();

	public void save(File path, boolean override) throws FileAlreadyExistsException, Exception;

	public void load() throws FileNotFoundException;

	public void delete();

	public void createTable(String tableName, String... headers);

	public void dropTable(String... tableNames);

	public void alterTable(String tableName, String header, Operator operate);

	public void insert(String tableName, Object... values);

	public void insert(Object... values);

	public void delete(Condition[] cond, String[] headers, Object[] values, String tableName) throws SQLException;

	public void update(Condition[] cond, String[] headers, Object[] values, String header, Object[] newValues,
			String tableName);

	public Object[][] select(Condition[] cond, String[] headers, Object[] values, String tableName,
			String... headersToShow);
}