package io.cronox.delta;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class CliPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("Delta:>", 
            AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA)
        );
    }
}

