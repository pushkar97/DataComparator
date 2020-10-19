package io.cronox.delta.helpers.shellHelpers;

import io.cronox.delta.data.DataSet;
import io.cronox.delta.data.Row;
import io.cronox.delta.models.DatasetExtract;
import org.springframework.shell.table.TableModel;

import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class DataSetTableModel extends TableModel {

    List<Row> data;

    private DataSetTableModel(List<Row> data) {this.data = data;}

    public static DataSetTableModelBuilder builder(DataSet dataSet){
        return new DataSetTableModelBuilder(dataSet);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return data.get(0).size();
    }

    @Override
    public Object getValue(int row, int column) {
        return data.get(row).get(column);
    }

    public static class DataSetTableModelBuilder {

        List<Row> data;
        DataSet dataSet;
        long limit = 0;
        DatasetExtract extract = DatasetExtract.DATA;

        public DataSetTableModelBuilder(@NotNull DataSet dataSet){
            this.dataSet = dataSet;
        }

        public DataSetTableModelBuilder limit(long limit){
            this.limit = limit;
            return this;
        }

        public DataSetTableModelBuilder extract(DatasetExtract extract){
            this.extract = extract;
            return this;
        }

        public DataSetTableModel build(){

            if(limit != 0)
                data = dataSet.getData(this.extract).stream().limit(this.limit).collect(Collectors.toCollection(LinkedList::new));
            else
                data = new LinkedList<>(dataSet.getData(this.extract));

            data.add(0,dataSet.getHeader());

            return new DataSetTableModel(data);
        }
    }
}
