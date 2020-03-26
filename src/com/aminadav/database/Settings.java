package com.aminadav.database;

import java.nio.charset.Charset;

/**
 * Settings: a class that stores and represents the settings of the Database
 * file.
 * 
 * @author Aminadav
 */
public class Settings {
	final Charset CHARSET;
	final String SEPARATOR;
	public final String END;
	final boolean DUPLICATE_ALLOWED;

	/**
	 * Default Settings: Charset: System encoding, Separator = "\t", End of the file
	 * name: ".adb" (Aminadav's DataBase) Duplicated lines are not allowed.
	 */
	public Settings() {
		CHARSET = Charset.forName(System.getProperty("file.encoding"));
		SEPARATOR = "\t";
		END = ".adb";
		DUPLICATE_ALLOWED = false;
	}

	/**
	 * Default Settings: Charset: System encoding, Separator = "\t", End of the file
	 * name: ".adb" (Aminadav's DataBase)
	 * 
	 * @param duplicateAllowed Specify if duplicated lines are allowed.
	 */
	public Settings(boolean duplicateAllowed) {
		CHARSET = Charset.forName(System.getProperty("file.encoding"));
		SEPARATOR = "\t";
		END = ".adb";
		DUPLICATE_ALLOWED = duplicateAllowed;
	}

	/**
	 * Default Settings: Charset: System encoding, End of the file name: ".adb"
	 * (Aminadav's DataBase)
	 * 
	 * @param duplicateAllowed Specify if duplicated lines are allowed.
	 * @param Separator        Specify a custom String separator between columns in
	 *                         the Database file.
	 */
	public Settings(boolean duplicateAllowed, String Separator) {
		CHARSET = Charset.forName(System.getProperty("file.encoding"));
		SEPARATOR = Separator;
		END = ".adb";
		DUPLICATE_ALLOWED = duplicateAllowed;
	}

	/**
	 * Default Settings: Charset: System encoding,
	 * 
	 * @param duplicateAllowed Specify if duplicated lines are allowed.
	 * @param Separator        Specify a custom String separator between columns in
	 *                         the Database file.
	 * @param end              Specify a custom String to add in the end of the file
	 *                         name.
	 */
	public Settings(boolean duplicateAllowed, String Separator, String end) {
		CHARSET = Charset.forName(System.getProperty("file.encoding"));
		SEPARATOR = Separator;
		END = end;
		DUPLICATE_ALLOWED = duplicateAllowed;
	}
	
	public Settings(String end, boolean duplicateAllowed) {
		CHARSET = Charset.forName(System.getProperty("file.encoding"));
		SEPARATOR = "\t";
		END = end;
		DUPLICATE_ALLOWED = duplicateAllowed;
	}
	
	public Settings(String end) {
		CHARSET = Charset.forName(System.getProperty("file.encoding"));
		SEPARATOR = "\t";
		END = end;
		DUPLICATE_ALLOWED = false;
	}

	/**
	 * Fully customized settings:
	 * 
	 * @param duplicateAllowed Specify if duplicated lines are allowed.
	 * @param Separator        Specify a custom String separator between columns in
	 *                         the Database file.
	 * @param end              Specify a custom String to add in the end of the file
	 *                         name.
	 * @param charsetName      Specify a custom encoding to use for file read &
	 *                         write operations.
	 */
	public Settings(boolean duplicateAllowed, String Separator, String end, String charsetName) {
		CHARSET = Charset.forName(charsetName);
		SEPARATOR = Separator;
		END = end;
		DUPLICATE_ALLOWED = duplicateAllowed;
	}
}