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

    long actualSize;
    List<Row> data;

    private DataSetTableModel(List<Row> data, long actualSize) {this.data = data; this.actualSize = actualSize;}

    public static DataSetTableModelBuilder builder(DataSet dataSet){
        return new DataSetTableModelBuilder(dataSet);
    }

    public long getActualSize(){
        return actualSize;
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
        long limit = -1;

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
            long actualSize = dataSet.getData(this.extract).size();
            if(limit != -1)
                data = dataSet.getData(this.extract).stream().limit(this.limit).collect(Collectors.toCollection(LinkedList::new));
            else
                data = new LinkedList<>(dataSet.getData(this.extract));

            data.add(0,dataSet.getHeader());

            return new DataSetTableModel(data, actualSize);
        }
    }
}
