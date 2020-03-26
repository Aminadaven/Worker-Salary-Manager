package com.aminadav.wsm;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Strings {
	private static final String BUNDLE_NAME = "com.aminadav.wsm.Strings"; //$NON-NLS-1$

	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Strings() {
	}
	
	public static void setLocale(Locale l) {
		RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, l);
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
