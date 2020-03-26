package com.aminadav.database;

interface Condition {
	boolean isRequestedValue(Object testedValue, Object conditionValue);
}