package com.aminadav.wsm;

import java.time.Month;

import javax.swing.JTable;

public class Worker {
	static String[] HEADERS = { Strings.getString("Worker.month"), Strings.getString("Worker.work_hours"), Strings.getString("Worker.month_salary") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	String name;
	double salaryPerHour;
	JTable table = null;
	
	public Worker(String name, double salaryPerHour) {
		this.name = name;
		this.salaryPerHour = salaryPerHour;
	}
	
	@Override
	public String toString() {
		return name;
	}

	void addHours(Month month, double hours) {
		Object[][] data = null;
		if (table == null) {
			data = new Object[1][3];
			data[0][0] = month;
			data[0][1] = hours;
			data[0][2] = hours * salaryPerHour;
		} else {
			data = getData();
			for(int i=0;i<data.length;i++) {
				if(data[i][0].equals(month)) {
					data[i][1] = (double) data[i][1] + hours;
					data[i][2] = (double) data[i][1] * salaryPerHour;
					return;
				}
			}
			data = addLine(month, hours);
		}
		table = new JTable(data, HEADERS);
		table.setFillsViewportHeight(true);
	}

	private Object[][] addLine(Month month, double hours) {
		Object[][] data = new Object[table.getRowCount() + 1][HEADERS.length];
		for (int i = 0; i < table.getRowCount(); i++) {
			for (int j = 0; j < HEADERS.length; j++) {
				data[i][j] = table.getValueAt(i, j);
			}
		}
		data[data.length - 1][0] = month;
		data[data.length - 1][1] = hours;
		data[data.length - 1][2] = (double) data[data.length - 1][1] * salaryPerHour;
		return data;
	}

	private Object[][] getData() {
		Object[][] data = new Object[table.getRowCount()][HEADERS.length];
		for (int i = 0; i < table.getRowCount(); i++) {
			for (int j = 0; j < HEADERS.length; j++) {
				data[i][j] = table.getValueAt(i, j);
			}
		}
		return data;
	}
}