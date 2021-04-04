package com.etheller.warsmash.desktop.editor.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class ExceptionPopup {
	public static void display(final Throwable e) {

		final JTextPane pane = new JTextPane();
		final OutputStream stream = new OutputStream() {
			public void updateStreamWith(final String s) {
				final Document doc = pane.getDocument();
				try {
					doc.insertString(doc.getLength(), s, null);
				}
				catch (final BadLocationException e) {
					JOptionPane.showMessageDialog(null, "MDL open error popup failed to create info popup.");
					e.printStackTrace();
				}
			}

			@Override
			public void write(final int b) throws IOException {
				updateStreamWith(String.valueOf((char) b));
			}

			@Override
			public void write(final byte[] b, final int off, final int len) throws IOException {
				updateStreamWith(new String(b, off, len));
			}

			@Override
			public void write(final byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
		final PrintStream ps = new PrintStream(stream);
		ps.println("Unknown error occurred:");
		e.printStackTrace(ps);
		JOptionPane.showMessageDialog(null, pane);
	}

	public static void display(final String s, final Exception e) {

		final JTextPane pane = new JTextPane();
		final OutputStream stream = new OutputStream() {
			public void updateStreamWith(final String s) {
				final Document doc = pane.getDocument();
				try {
					doc.insertString(doc.getLength(), s, null);
				}
				catch (final BadLocationException e) {
					JOptionPane.showMessageDialog(null, "MDL open error popup failed to create info popup.");
					e.printStackTrace();
				}
			}

			@Override
			public void write(final int b) throws IOException {
				updateStreamWith(String.valueOf((char) b));
			}

			@Override
			public void write(final byte[] b, final int off, final int len) throws IOException {
				updateStreamWith(new String(b, off, len));
			}

			@Override
			public void write(final byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
		final PrintStream ps = new PrintStream(stream);
		ps.println(s + ":");
		e.printStackTrace(ps);
		JOptionPane.showMessageDialog(null, pane);
	}
}
