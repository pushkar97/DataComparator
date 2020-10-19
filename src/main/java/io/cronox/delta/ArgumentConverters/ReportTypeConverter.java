package io.cronox.delta.ArgumentConverters;

import io.cronox.delta.models.ReportType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReportTypeConverter implements Converter<String, ReportType> {

    @Override
    public ReportType convert(String source) {
        return ReportType.valueOf(source);
    }
}
