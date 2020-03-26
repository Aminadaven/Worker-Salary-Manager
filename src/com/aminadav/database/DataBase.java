package com.aminadav.database;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class DataBase extends DatabaseSkeleton {
	public DataBase(String user) {
		super(user);
	}

	public DataBase(String user, Settings settings) {
		super(user, settings);
	}

	@Override
	public void save(File path, boolean override) throws Exception, FileAlreadyExistsException {
		// file settings
		Path file = Paths.get(path.getAbsolutePath() + System.getProperty("file.separator") + USER + settings.END);
		if (!override && file.toFile().exists())
			throw new FileAlreadyExistsException(
					"Error - The file: \"" + USER + settings.END + "\" is already exists!");
		// preparing the text to write
		String toWrite = "";
		// each table
		Table[] tableArr = convertVector(tables);
		if (tableArr.length < 1)
			throw new Exception();
		for (Table cTable : tableArr) {
			// HEADERs
			toWrite += "Table/" + cTable.NAME;
			Column[] columns = conver(cTable);
			for (Column current : columns) {
				toWrite += settings.SEPARATOR + current.HEADER;
			}
			// actual data
			for (int i = 0; i < columns[0].size(); i++) {
				toWrite += "\n" + i;
				for (int j = 0; j < columns.length; j++) {
					toWrite += settings.SEPARATOR + columns[j].get(i).toString();
				}
			}
			toWrite += "\n";
		}
		toWrite = toWrite.substring(0, toWrite.length() - 1).trim();
		// Convert the string to a ByteBuffer.
		ByteBuffer bb = ByteBuffer.wrap(toWrite.getBytes());
		// writing the encrypted data
		try (SeekableByteChannel sbc = Files.newByteChannel(file, CREATE, WRITE)) {
			sbc.write(bb);
		} catch (IOException x) {
			System.out.println("Exception thrown: " + x);
		}
	}

	@Override
	public void load() throws FileNotFoundException {
		Path file = Paths.get(USER + settings.END);
		if (!file.toFile().exists())
			throw new FileNotFoundException("Error - The file: \"" + USER + settings.END + "\" is not exists!");
		// Defaults to READ
		try (SeekableByteChannel byteChannel = Files.newByteChannel(file)) {
			ByteBuffer buf = ByteBuffer.allocate((int) file.toFile().length());
			Vector<Byte> bytes = new Vector<Byte>();
			while (byteChannel.read(buf) > 0) {
				buf.rewind();
				for (byte cByte : buf.array())
					bytes.add(cByte);
				buf.flip();
			}
			byte[] sum = new byte[bytes.size()];
			for (int i = 0; i < sum.length; i++) {
				sum[i] = bytes.get(i);
			}
			String decrypted = new String(settings.CHARSET.decode(ByteBuffer.wrap(sum)).array());
			String[] lines = decrypted.split("\n");
			String[] bl;
			tables = new Vector<Table>();
			Table nTable = null;
			for (String currentLine : lines) {
				bl = currentLine.split(settings.SEPARATOR);
				String[] words = bl[0].split("/");
				if (words[0].equals("Table")) {
					nTable = new Table(words[1]);
					for (int i = 0; i < bl.length - 1; i++) {
						nTable.add(new Column(bl[i + 1]));
					}
					tables.add(nTable);
				} else {
					if (nTable != null) {
						for (int i = 0; i < nTable.size(); i++) {
							nTable.get(i).add(bl[i + 1]);
						}
					} else {
						System.err.print("Error: Corrupted File, Or Wrong Password!");
						throw new IOException();
					}
				}
			}
			System.out.println("The DataBase has been successfully loaded.");
		} catch (IOException x) {
			System.err.println("The DataBase has not been loaded.\nFor details:");
			System.err.format("IOException: %s%n", x);
		}
	}

	@Override
	public void delete() {
		Paths.get(USER + settings.END).toFile().delete();
		tables.removeAllElements();
		tables = null;
	}

	@Override
	public void createTable(String tableName, String... headers) {
		Table toAdd = new Table(tableName);
		for (String header : headers) {
			toAdd.add(new Column(header));
		}
		tables.add(toAdd);
	}

	@Override
	public void dropTable(String... tableNames) {
		Table[] tableArr = convertVector(tables);
		for (Table cTable : tableArr) {
			for (String tableName : tableNames) {
				if (cTable.NAME.equals(tableName))
					tables.remove(cTable);
			}
		}
	}

	@Override
	public void alterTable(String tableName, String header, Operator operate) {
		Table[] tableArr = convertVector(tables);
		for (Table cTable : tableArr) {
			if (cTable.NAME.equals(tableName)) {
				switch (operate) {
				case ADD:
					cTable.add(new Column(header));
					break;
				case REMOVE:
					for (Column toRemove : (Column[]) cTable.toArray()) {
						if (toRemove.HEADER.equals(header))
							cTable.remove(toRemove);
						break;
					}
					break;
				}
			}
		}
	}

	protected Table[] convertVector(Vector<Table> tables) {
		Table[] tableArr = new Table[tables.size()];
		for (int i = 0; i < tableArr.length; i++) {
			tableArr[i] = (Table) tables.get(i);
		}
		return tableArr;
	}

	protected Column[] conver(Table columns) {
		Column[] tableArr = new Column[columns.size()];
		for (int i = 0; i < tableArr.length; i++) {
			tableArr[i] = (Column) columns.get(i);
		}
		return tableArr;
	}

	@Override
	public void insert(String tableName, Object... values) {
		Table[] tableArr = convertVector(tables);
		for (Table cTable : tableArr) {
			if (cTable.NAME.equals(tableName)) {
				Column[] columns = conver(cTable);
				for (int i = 0; i < columns.length; i++) {
					columns[i].add(values[i]);
				}
				return;
			}
		}
	}

	@Override
	public void insert(Object... values) {
		Column[] columns = conver(tables.get(0));
		for (int i = 0; i < columns.length; i++) {
			columns[i].add(values[i]);
		}
	}

	@Override
	public Object[][] select(Condition[] cond, String[] headers, Object[] values, String tableName,
			String... headersToShow) {
		Vector<Integer> toSelect = filter(cond, headers, values, tableName);
		Table t = findTable(tableName);
		Object[][] data = new Object[headersToShow.length][toSelect.size()];
		for (int i = 0; i < headersToShow.length; i++) {
			Column c = findColumn(t, headersToShow[i]);
			for (int j = 0; j < toSelect.size(); j++) {
				data[i][j] = c.get(toSelect.get(j));
			}
		}
		return data;
	}

	@Override
	public void delete(Condition[] cond, String[] headers, Object[] values, String tableName) {
		Table[] tableArr = convertVector(tables);
		for (Table cTable : tableArr) {
			// for (String tableName : tableNames) {
			if (cTable.NAME.equals(tableName)) {
				// GVector<String> tableHeaders = new GVector<>();
				/*
				 * for(String header:headers) { if(stringBreaker(header)[0].equals(tableName))
				 * tableHeaders.add(stringBreaker(header)[1]); }
				 */
				Vector<Integer> toRemove = new Vector<>();
				boolean[] condsVals = new boolean[cond.length];
				for (int i = 0; i < cTable.get(0).size(); i++) {// i is line number
					Column[] columns = conver(cTable);
					for (Column col : columns) {
						for (int j = 0; j < headers.length; j++) {
							if (col.HEADER.equals(headers[j])) {
								condsVals[j] = cond[j].isRequestedValue(col.get(i), values[j]);
							}
						}
					}
					// suppose multiple conditions are && ANDs;
					for (int b = 0; b < condsVals.length; b++) {
						if (!condsVals[b])
							break;
						if (b == condsVals.length - 1)
							toRemove.add(i);
					}
				}
				for (int r = 0; r < toRemove.size(); r++) {
					Column[] columns2 = conver(cTable);
					for (Column col : columns2) {
						col.remove(toRemove.get(r));
					}
				}
				// }
			}
			// }
		}

	}

	@Override
	public void update(Condition[] cond, String[] headers, Object[] values, String header, Object[] newValues,
			String tableName) {
		Vector<Integer> toUpdate = filter(cond, headers, values, tableName);
		Table t = findTable(tableName);
		Column c = findColumn(t, header);
		for (int r = 0; r < toUpdate.size(); r++) {
			c.set(toUpdate.get(r), newValues[r]);
		}
	}

	private Column findColumn(Table t, String header) {
		Column[] columns = conver(t);
		for (Column col : columns) {
			if (col.HEADER.equals(header)) {
				return col;
			}
		}
		return null;
	}

	private Table findTable(String tableName) {
		Table[] tableArr = convertVector(tables);
		for (Table table : tableArr) {
			if (table.NAME.equals(tableName)) {
				return table;
			}
		}
		return null;
	}

	Vector<Integer> filter(Condition[] cond, String[] headers, Object[] values, String tableName) {
		Vector<Integer> filtered = new Vector<>();
		boolean[] condsVals = new boolean[cond.length];
		Table[] tableArr = convertVector(tables);
		for (Table table : tableArr) {
			if (table.NAME.equals(tableName)) {
				for (int i = 0; i < table.get(0).size(); i++) {// i is line number
					Column[] columns = conver(table);
					for (Column col : columns) {
						for (int j = 0; j < headers.length; j++) {
							if (col.HEADER.equals(headers[j])) {
								condsVals[j] = cond[j].isRequestedValue(col.get(i), values[j]);
							}
						}
					}
					// suppose multiple conditions are && ANDs;
					for (int b = 0; b < condsVals.length; b++) {
						if (!condsVals[b])
							break;
						if (b == condsVals.length - 1)
							filtered.add(i);
					}
				}
			}
		}
		return filtered;
	}

	/*
	 * private String[] stringBreaker(String header) { return header.split("."); }
	 */
}