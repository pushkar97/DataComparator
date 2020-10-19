package io.cronox.delta.commands;

import io.cronox.delta.connection.DataSourceConnection;
import io.cronox.delta.data.CellFactory;
import io.cronox.delta.helpers.shellHelpers.DataSetTableModel;
import io.cronox.delta.helpers.shellHelpers.ShellHelper;
import lombok.var;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ShellComponent
public class DataVisualizationCommands {

    ShellHelper shellHelper;

    CellFactory cellFactory;

    public DataVisualizationCommands(ShellHelper shellHelper, CellFactory cellFactory){
        this.shellHelper = shellHelper;
        this.cellFactory = cellFactory;
    }

    @ShellMethod(value = "view data returned from query")
    public String view(@ShellOption(value = {"-c", "--conn"}) DataSourceConnection connection,
                       @ShellOption(value = {"-q", "--query"}) String query,
                       @ShellOption(value = {"-l", "--limit"}, defaultValue="0") long max,
                       @ShellOption(value = {"-b", "--border"}, defaultValue = "oldschool") BorderStyle borderStyle) throws IOException {

        var sourceFile = new File(query);
        if(!sourceFile.exists()){
            shellHelper.printWarning("Provided file path for query is not valid, parsing as a query...");
        }else {
            shellHelper.printInfo("Reading source query string from " + sourceFile.getAbsolutePath());
            query = new String(Files.readAllBytes(Paths.get(sourceFile.getAbsolutePath())));
        }

        var data = connection.getDataSetGenerator(cellFactory).generate(query);
        DataSetTableModel model = DataSetTableModel.builder(data).limit(max).build();
        var tableBuilder = new TableBuilder(model);
        tableBuilder.addHeaderAndVerticalsBorders(borderStyle);
        shellHelper.print(tableBuilder.build().render(140));
        return shellHelper.getInfoMessage("Total rows: " + model.getActualSize() +", Currently displayed: " + (model.getRowCount() - 1));
    }
}
