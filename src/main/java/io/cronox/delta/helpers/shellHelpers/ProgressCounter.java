package io.cronox.delta.helpers.shellHelpers;

import org.jline.terminal.Terminal;

public class ProgressCounter {
	private static final String CUU = "\u001B[A";
	
	private Terminal terminal;
	private char[] spinner = {'◜','◝','◞','◟'}; //{ '|', '/', '-', '\\' };
	
	private boolean started = false;
	private int spinCounter = 0;
	private String pattern = " %s: %d ";

	public ProgressCounter(Terminal terminal) {
		this(terminal, null);
	}

	public ProgressCounter(Terminal terminal, String pattern) {
		this(terminal, pattern, null);
	}

	public ProgressCounter(Terminal terminal, String pattern, char[] spinner) {
		this.terminal = terminal;

		if (pattern != null) {
			this.pattern = pattern;
		}
		if (spinner != null) {
			this.spinner = spinner;
		}
	}

	public void display() {
		display(null);
	}

	public void display(int count, String message) {
		if (!started) {
			terminal.writer().println();
			started = true;
		}
		String progress = String.format(pattern, message, count);

		terminal.writer().println(CUU + "\r" + getSpinnerChar() + progress);
		terminal.flush();
	}

	public void display(String message) {
		if (!started) {
			terminal.writer().println();
			started = true;
		}
		message = message == null ? "" : message;
		terminal.writer().println(CUU + "\r" + getSpinnerChar() + message);
		terminal.flush();
	}

	public void reset() {
		spinCounter = 0;
		started = false;
	}

	private char getSpinnerChar() {
		char spinChar = spinner[spinCounter];
		spinCounter++;
		if (spinCounter == spinner.length) {
			spinCounter = 0;
		}
		return spinChar;
	}

	// --- set / get methods ---------------------------------------------------

	public char[] getSpinner() {
		return spinner;
	}

	public void setSpinner(char[] spinner) {
		this.spinner = spinner;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}