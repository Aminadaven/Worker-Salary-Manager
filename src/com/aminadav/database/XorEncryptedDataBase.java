package com.aminadav.database;

public class XorEncryptedDataBase extends EncryptedDataBase {
	public XorEncryptedDataBase(String user, String password) {
		super(user, password);
	}

	public XorEncryptedDataBase(String user, String password, Settings settings) {
		super(user, password, settings);
	}

	public byte[] crypt(byte[] data) {
		byte[] pass = PASS.getBytes();
		byte[] cryptedBytes = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			cryptedBytes[i] = (byte) (data[i] ^ pass[i % pass.length]);
		}
		return cryptedBytes;
	}
}