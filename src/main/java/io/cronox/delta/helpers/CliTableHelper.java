package io.cronox.delta.helpers;

import io.cronox.delta.data.DataSet;
import io.cronox.delta.helpers.shellHelpers.DataSetTableModel;
import io.cronox.delta.helpers.shellHelpers.ShellHelper;
import io.cronox.delta.models.DatasetExtract;
import lombok.var;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.stereotype.Component;

@Component
public class CliTableHelper {

    ShellHelper helper;

    @Value("${cli.table.total_available_width}")
    int totalAvailableWidth;

    CliTableHelper(ShellHelper helper){
        this.helper = helper;
    }

    public void printDataTable(DataSet data, String title, long max, @org.jetbrains.annotations.NotNull DatasetExtract extract, BorderStyle borderStyle) {
        helper.printInfo("\n\n" + title);
        if ((extract.equals(DatasetExtract.DATA) && data.size() != 0) ||
                (extract.equals(DatasetExtract.DUPLICATES) && data.getDuplicates().size() != 0)) {

            DataSetTableModel model = DataSetTableModel.builder(data).limit(max).extract(extract).build();
            var tableBuilder = new TableBuilder(model);
            tableBuilder.addHeaderAndVerticalsBorders(borderStyle);
            helper.print(tableBuilder.build().render(totalAvailableWidth));
            helper.printInfo("Total rows: " + model.getActualSize() +", Currently displayed: " + (model.getRowCount() - 1));
        }else {
            helper.printWarning("No data to display : 0 rows found");
        }
    }
}
