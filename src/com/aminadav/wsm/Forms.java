package com.aminadav.wsm;

import java.awt.Dialog.ModalityType;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDate;
import java.time.Month;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public final class Forms {
	static Object[] addWorker(Window caller) {
		JDialog workerDialog = new JDialog(caller, Strings.getString("Forms.add_worker_dialog"), ModalityType.APPLICATION_MODAL); //$NON-NLS-1$
		Object[] results = new Object[2];
		JLabel lName = new JLabel(Strings.getString("Forms.enter_worker_name")), lHS = new JLabel(Strings.getString("Forms.enter_sph")); //$NON-NLS-1$ //$NON-NLS-2$
		JTextField tfName = new JTextField(20), tfHS = new JTextField(20);
		tfName.setText(System.getProperty("user.name")); //$NON-NLS-1$
		JButton send = new JButton(Strings.getString("Forms.add_worker")); //$NON-NLS-1$

		tfHS.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}

			@Override
			public void keyReleased(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}

			@Override
			public void keyTyped(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}
		});

		send.addActionListener(p -> {
			results[0] = tfName.getText();
			results[1] = Double.parseDouble(tfHS.getText());
			workerDialog.dispose();
		});

		workerDialog.setLayout(new GridLayout(5, 1));
		workerDialog.setTitle(Strings.getString("Forms.add_worker_dialog")); //$NON-NLS-1$
		workerDialog.add(lName);
		workerDialog.add(tfName);
		workerDialog.add(lHS);
		workerDialog.add(tfHS);
		workerDialog.add(send);
		workerDialog.pack();
		workerDialog.setLocationRelativeTo(null);
		workerDialog.setVisible(true);

		return results;
	}

	static Object[] addHours(Window caller, Worker... names) {
		JDialog hoursDialog = new JDialog(caller, Strings.getString("Forms.add_hours_dialog"), ModalityType.APPLICATION_MODAL); //$NON-NLS-1$
		Object[] results = new Object[3];
		JLabel lName = new JLabel(Strings.getString("Forms.enter_worker_name")), //$NON-NLS-1$
				lHours = new JLabel(Strings.getString("Forms.hours_to_add")), //$NON-NLS-1$
				lMonth = new JLabel(Strings.getString("Forms.select_month")); //$NON-NLS-1$
		JComboBox<Worker> cbNames = new JComboBox<>(names);
		JTextField tfHours = new JTextField(20);
		JComboBox<Month> cbMonths = new JComboBox<>(Month.values());
		cbMonths.setSelectedIndex(LocalDate.now().getMonth().getValue() - 1);
		JButton send = new JButton(Strings.getString("Forms.add_hours")); //$NON-NLS-1$

		tfHours.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}

			@Override
			public void keyReleased(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}

			@Override
			public void keyTyped(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}
		});

		cbMonths.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}

			@Override
			public void keyReleased(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}

			@Override
			public void keyTyped(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}
		});

		send.addActionListener(p -> {
			results[0] = cbNames.getSelectedItem();
			results[1] = Double.parseDouble(tfHours.getText());
			results[2] = cbMonths.getSelectedItem();
			hoursDialog.dispose();
		});

		hoursDialog.setLayout(new GridLayout(7, 1));
		hoursDialog.setTitle(Strings.getString("Forms.add_hours_dialog")); //$NON-NLS-1$
		hoursDialog.add(lName);
		hoursDialog.add(cbNames);
		hoursDialog.add(lHours);
		hoursDialog.add(tfHours);
		hoursDialog.add(lMonth);
		hoursDialog.add(cbMonths);
		hoursDialog.add(send);
		hoursDialog.pack();
		hoursDialog.setLocationRelativeTo(null);
		hoursDialog.setVisible(true);

		return results;
	}

	/*
	 * static Object[] addHours(Window caller) { JDialog hoursDialog = new
	 * JDialog(caller, "Add Hours Dialog", ModalityType.APPLICATION_MODAL); Object[]
	 * results = new Object[2]; JLabel lHours = new
	 * JLabel("Enter the number of hours that you have worked: "), lMonth = new
	 * JLabel("Select Month: "); JTextField tfHours = new JTextField(20);
	 * JComboBox<Month> cbMonths = new JComboBox<>(Month.values());
	 * cbMonths.setSelectedIndex(LocalDate.now().getMonth().getValue()); JButton
	 * send = new JButton("Add Hours");
	 * 
	 * send.addActionListener(p -> { results[0] =
	 * Integer.parseInt(tfHours.getText()); results[1] = cbMonths.getSelectedItem();
	 * hoursDialog.dispose(); });
	 * 
	 * hoursDialog.setLayout(new GridLayout(5, 1)); //
	 * hoursDialog.setTitle("Add Hours Dialog"); hoursDialog.add(lHours);
	 * hoursDialog.add(tfHours); hoursDialog.add(lMonth); hoursDialog.add(cbMonths);
	 * hoursDialog.add(send); hoursDialog.pack();
	 * hoursDialog.setLocationRelativeTo(null); hoursDialog.setVisible(true);
	 * 
	 * return results; }
	 */

	static String[] getUserNPass(Window caller) {
		JDialog passD = new JDialog(caller, Strings.getString("Forms.enc_db_setup"), ModalityType.APPLICATION_MODAL); //$NON-NLS-1$
		String[] results = new String[2];
		JLabel ln = new JLabel(Strings.getString("Forms.enter_db_name")), lp = new JLabel(Strings.getString("Forms.enter_db_pass")); //$NON-NLS-1$ //$NON-NLS-2$
		JTextField name = new JTextField(20);
		name.setText(System.getProperty("user.name")); //$NON-NLS-1$
		JPasswordField pass = new JPasswordField(20);
		JButton send = new JButton(Strings.getString("Forms.ok")); //$NON-NLS-1$

		name.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}

			@Override
			public void keyReleased(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}

			@Override
			public void keyTyped(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}
		});

		pass.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}

			@Override
			public void keyReleased(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}

			@Override
			public void keyTyped(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
			}
		});

		send.addActionListener(p -> {
			results[0] = name.getText();
			results[1] = new String(pass.getPassword());
			passD.dispose();
		});

		passD.setLayout(new GridLayout(5, 1));
		passD.setTitle(Strings.getString("Forms.enc_db_setup2")); //$NON-NLS-1$
		passD.add(ln);
		passD.add(name);
		passD.add(lp);
		passD.add(pass);
		passD.add(send);
		passD.pack();
		passD.setLocationRelativeTo(null);
		passD.setVisible(true);

		return results;
	}
	/*
	 * static String[] getHeader() { JDialog passD = new JDialog(null, "Dialog",
	 * ModalityType.APPLICATION_MODAL); String[] results = new String[2]; JLabel ln
	 * = new JLabel("Enter Column Header: "), lp = new
	 * JLabel("Choose Column Type: "); JRadioButton str = new
	 * JRadioButton("String"); JRadioButton iNt = new JRadioButton("Integer");
	 * JRadioButton flt = new JRadioButton("Float"); ButtonGroup g = new
	 * ButtonGroup(); g.add(str); g.add(iNt); g.add(flt); iNt.setSelected(true);
	 * results[1] = "int"; JTextField name = new JTextField(20); JButton send = new
	 * JButton("Send data to program");
	 * 
	 * str.addActionListener(p -> results[1] = "String"); iNt.addActionListener(p ->
	 * results[1] = "int"); flt.addActionListener(p -> results[1] = "Float");
	 * 
	 * send.addActionListener(p -> { results[0] = name.getText(); passD.dispose();
	 * });
	 * 
	 * passD.setLayout(new GridLayout(7, 1)); passD.setTitle("New Column");
	 * passD.add(ln); passD.add(name); passD.add(lp); passD.add(str);
	 * passD.add(iNt); passD.add(flt); passD.add(send); passD.pack();
	 * passD.setLocationRelativeTo(null); passD.setVisible(true);
	 * 
	 * return results; }
	 */
}