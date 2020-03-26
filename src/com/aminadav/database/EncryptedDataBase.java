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

abstract class EncryptedDataBase extends DataBase implements SecuredDB {
	protected final String PASS;

	public EncryptedDataBase(String user, String password) {
		super(user);
		PASS = password;
	}

	public EncryptedDataBase(String user, String password, Settings settings) {
		super(user, settings);
		PASS = password;
	}

	public abstract byte[] crypt(byte[] data);

	@Override
	public byte[] encrypt(String data) {
		return crypt(data.getBytes());
	}

	@Override
	public byte[] encrypt(byte[] data) {
		return crypt(data);
	}

	@Override
	public byte[] decrypt(byte[] data) {
		return crypt(data);
	}

	@Override
	public void save(File path, boolean override) throws Exception {
		// file settings
		Path file = Paths.get(path.getAbsolutePath() + System.getProperty("file.separator") + USER + settings.END);
		if (!override && file.toFile().exists())
			throw new FileAlreadyExistsException("Error - The file: \"" + USER + settings.END + "\" is already exist!");
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
		// Encrypting & Convert the string to a ByteBuffer.
		byte data[] = encrypt(toWrite);
		ByteBuffer bb = ByteBuffer.wrap(data);
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
			byte[] decripted = decrypt(sum);
			String decrypted = new String(settings.CHARSET.decode(ByteBuffer.wrap(decripted)).array());
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
						System.err.println(
								"Error: The File: " + file.toAbsolutePath() + " Is Corrupted File, Or Wrong Password!");
						throw new IOException();
					}
				}
			}
			System.out.println("The DataBase has been successfully loaded.");
		} catch (IOException x) {
			System.err.println("The DataBase has not been loaded.\nFor details:");
			x.printStackTrace();
			System.err.format("IOException: %s%n", x);
		}
	}
}