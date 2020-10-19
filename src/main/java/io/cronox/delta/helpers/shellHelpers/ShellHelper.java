package io.cronox.delta.helpers.shellHelpers;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;

public class ShellHelper {

	@Value("${shell.out.info}")
	public String infoColor;

	@Value("${shell.out.success}")
	public String successColor;

	@Value("${shell.out.warning}")
	public String warningColor;

	@Value("${shell.out.error}")
	public String errorColor;

	private final Terminal terminal;

	public ShellHelper(Terminal terminal) {
		this.terminal = terminal;
	}

	public String getColored(String message, PromptColor color) {
		return (new AttributedStringBuilder())
				.append(message, AttributedStyle.DEFAULT.foreground(color.toJlineAttributedStyle())).toAnsi();
	}

	public String getInfoMessage(String message) {
		return getColored(message, PromptColor.valueOf(infoColor));
	}

	public String getSuccessMessage(String message) {
		return getColored(message, PromptColor.valueOf(successColor));
	}

	public String getWarningMessage(String message) {
		return getColored(message, PromptColor.valueOf(warningColor));
	}

	public String getErrorMessage(String message) {
		return getColored(message, PromptColor.valueOf(errorColor));
	}

	public void print(String message) {
		print(message, null);
	}

	public void printSuccess(String message) {
		print(message, PromptColor.valueOf(successColor));
	}

	public void printInfo(String message) {
		print(message, PromptColor.valueOf(infoColor));
	}

	public void printWarning(String message) {
		print(message, PromptColor.valueOf(warningColor));
	}

	public void printError(String message) {
		print(message, PromptColor.valueOf(errorColor));
	}

	public void print(String message, PromptColor color) {
		String toPrint = message;
		if (color != null) {
			toPrint = getColored(message, color);
		}
		terminal.writer().println(toPrint);
		terminal.flush();
	}


	
	public Terminal getTerminal() {
		return terminal;
	}
	
	
}