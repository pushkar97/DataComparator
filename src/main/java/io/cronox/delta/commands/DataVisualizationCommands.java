package io.cronox.delta.commands;

import io.cronox.delta.connection.DataSourceConnection;
import io.cronox.delta.data.CellFactory;
import io.cronox.delta.helpers.CliTableHelper;
import io.cronox.delta.helpers.shellHelpers.ShellHelper;
import io.cronox.delta.models.DatasetExtract;
import lombok.var;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BorderStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ShellComponent
public class DataVisualizationCommands {

    ShellHelper shellHelper;

    CliTableHelper cliTableHelper;

    CellFactory cellFactory;

    public DataVisualizationCommands(ShellHelper shellHelper,
                                     CellFactory cellFactory,
                                     CliTableHelper cliTableHelper){
        this.shellHelper = shellHelper;
        this.cellFactory = cellFactory;
        this.cliTableHelper = cliTableHelper;
    }

    @ShellMethod(value = "view data returned from query")
    public void view(@ShellOption(value = {"-c", "--conn"}) DataSourceConnection connection,
                       @ShellOption(value = {"-q", "--query"}) String query,
                       @ShellOption(value = {"-l", "--limit"}, defaultValue="-1") int max,
                       @ShellOption(value = {"-b", "--border"}, defaultValue = "oldschool") BorderStyle borderStyle) throws IOException {

        var sourceFile = new File(query);
        if(!sourceFile.exists()){
            shellHelper.printWarning("Provided file path for query is not valid, parsing as a query...");
        }else {
            shellHelper.printInfo("Reading source query string from " + sourceFile.getAbsolutePath());
            query = new String(Files.readAllBytes(Paths.get(sourceFile.getAbsolutePath())));
        }

        connection.setMaxRows(max);
        var data = connection.getDataSetGenerator(cellFactory).generate(query);

        cliTableHelper.printDataTable(data, "Result", max, DatasetExtract.DATA, borderStyle);
    }
}
