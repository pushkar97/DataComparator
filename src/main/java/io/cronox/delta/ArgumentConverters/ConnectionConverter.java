package io.cronox.delta.ArgumentConverters;


import io.cronox.delta.connection.DataSourceConnection;
import io.cronox.delta.repository.Connections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConnectionConverter implements Converter<String, DataSourceConnection> {

    @Autowired
    Connections connections;

    @Override
    public DataSourceConnection convert(String source) {
        return connections.get(source);
    }
}
