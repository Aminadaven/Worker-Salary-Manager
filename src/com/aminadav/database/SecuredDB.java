package com.aminadav.database;

interface SecuredDB extends DB {
	byte[] encrypt(String data);
	byte[] encrypt(byte[] data);
	byte[] decrypt(byte[] data);
}