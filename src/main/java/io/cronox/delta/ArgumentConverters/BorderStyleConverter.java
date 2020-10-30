package io.cronox.delta.ArgumentConverters;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.shell.table.BorderStyle;
import org.springframework.stereotype.Component;

@Component
public class BorderStyleConverter implements Converter<String, BorderStyle> {
    @Override
    public BorderStyle convert(@NotNull String source) {
        return BorderStyle.valueOf(source);
    }
}
