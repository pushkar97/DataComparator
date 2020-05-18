package io.cronox.delta;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import io.cronox.delta.helpers.shellHelpers.InputReader;
import io.cronox.delta.helpers.shellHelpers.ProgressBar;
import io.cronox.delta.helpers.shellHelpers.ProgressCounter;
import io.cronox.delta.helpers.shellHelpers.ShellHelper;

@Configuration
public class SpringShellConfig {

	@Bean
	public ShellHelper shellHelper(@Lazy Terminal terminal) {
		return new ShellHelper(terminal);
	}

	@Bean
	public InputReader inputReader(@Lazy LineReader lineReader, @Lazy ShellHelper shellHelper) {
		return new InputReader(lineReader, shellHelper);
	}

	@Bean
	public ProgressCounter progressCounter(@Lazy Terminal terminal) {
	    return new ProgressCounter(terminal);
	}
	
	@Bean
	public ProgressBar progressBar(@Lazy ShellHelper shellHelper) {
	    return new ProgressBar(shellHelper);
	}
}