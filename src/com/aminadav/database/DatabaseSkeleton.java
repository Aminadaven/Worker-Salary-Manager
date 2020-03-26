package com.aminadav.database;

import java.nio.file.Paths;
import java.util.Vector;

abstract class DatabaseSkeleton implements DB {
	protected Settings settings;
	public final String USER;
	public Vector<Table> tables = new Vector<Table>();

	public DatabaseSkeleton(String user) {
		USER = user;
		settings = new Settings();
	}

	public DatabaseSkeleton(String user, Settings settings) {
		USER = user;
		this.settings = settings;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public boolean exist() {
		return Paths.get(USER + settings.END).toFile().exists();
	}
}

abstract class SecuredDBSkeleton extends DatabaseSkeleton implements SecuredDB {
	protected final String PASS;

	public SecuredDBSkeleton(String user, String password) {
		super(user);
		PASS = password;
	}

	public SecuredDBSkeleton(String user, String password, Settings settings) {
		super(user, settings);
		PASS = password;
	}
}