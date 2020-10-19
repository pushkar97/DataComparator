package io.cronox.delta.dataSetGenerators;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Types;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import io.cronox.delta.data.CellFactory;
import io.cronox.delta.data.DataSet;
import io.cronox.delta.data.Row;
import io.cronox.delta.data.cellTypes.BigDecimalCell;
import io.cronox.delta.data.cellTypes.Cell;
import io.cronox.delta.data.cellTypes.DateCell;
import io.cronox.delta.data.cellTypes.DoubleCell;
import io.cronox.delta.data.cellTypes.FloatCell;
import io.cronox.delta.data.cellTypes.IntegerCell;
import io.cronox.delta.data.cellTypes.LongCell;
import io.cronox.delta.data.cellTypes.ShortCell;
import io.cronox.delta.data.cellTypes.StringCell;

public class JDBCDataSetGenerator implements DataSetGenerator {

    CellFactory cellBuilder;

    JdbcTemplate jdbcTemplate;

    Timer timer;

    private final PropertyChangeSupport support;

    private int rowsDone, rowsDoneOld;

    public JDBCDataSetGenerator(CellFactory builder, JdbcTemplate jdbcTemplate) {
        this.cellBuilder = builder;
        this.jdbcTemplate = jdbcTemplate;
        this.support = new PropertyChangeSupport(this);
        this.timer = new Timer("UpdateProgressTimer");
    }

    public void subscribe(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void updateProgress() {
        support.firePropertyChange("rowsDone", rowsDoneOld, rowsDone);
        rowsDoneOld = rowsDone;
    }

    @Override
    public DataSet generate(String sql) {
        DataSet data = new DataSet();
        SqlRowSet dbRow = jdbcTemplate.queryForRowSet(sql);
        SqlRowSetMetaData metaData = dbRow.getMetaData();
        // Set Column names
        Row colNames = new Row();
        Arrays.stream(metaData.getColumnNames()).map(StringCell::new).forEach(colNames::add);
        data.setHeader(colNames);

//		String columnTypes = IntStream.range(1, metaData.getColumnCount())
//			.mapToObj(c -> metaData.getColumnTypeName(c))
//			.collect(Collectors.joining(","));
        timer.schedule(new UpdateProgressTask(), 0, 200);
        while (dbRow.next()) {
            Row row = new Row();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                Cell temp;
                if (dbRow.getObject(i) == null) {
                    temp = cellBuilder.getNullCell();
                    row.add(temp);
                    continue;
                }
                //dbRow.wasNull()
                switch (metaData.getColumnType(i)) {
                    case Types.TINYINT:
                        temp = cellBuilder.getByteCell(dbRow.getByte(i));//new ByteCell(dbRow.getByte(i));
                        break;
                    case Types.SMALLINT:
                        temp = new ShortCell(dbRow.getShort(i));
                        break;
                    case Types.INTEGER:
                        temp = new IntegerCell(dbRow.getInt(i));
                        break;
                    case Types.BIGINT:
                        temp = new LongCell(dbRow.getLong(i));
                        break;
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.LONGNVARCHAR:
                        temp = new StringCell(dbRow.getString(i));
                        break;
                    case Types.BIT:
                    case Types.BOOLEAN:
                        //temp = new BooleanCell(dbRow.getBoolean(i));
                        temp = cellBuilder.getBooleanCell(dbRow.getBoolean(i));
                        break;
                    case Types.DOUBLE:
                        temp = new DoubleCell(dbRow.getDouble(i));
                        break;
                    case Types.REAL:
                    case Types.FLOAT:
                        temp = new FloatCell(dbRow.getFloat(i));
                        break;
                    case Types.NUMERIC:
                    case Types.DECIMAL:
                        temp = new BigDecimalCell(dbRow.getBigDecimal(i));
                        break;
                    case Types.DATE:
                        temp = new DateCell(dbRow.getDate(i));
                        break;
                    case Types.TIME:
                        temp = new DateCell(dbRow.getTime(i));
                        break;
                    case Types.TIMESTAMP:
                        temp = new DateCell(dbRow.getTimestamp(i));
                        break;
                    default:
                        temp = new StringCell(Objects.requireNonNull(dbRow.getObject(i)).toString());
                }
                row.add(temp);
            }
            data.add(row);
            ++rowsDone;
        }
        timer.cancel();
        updateProgress();
        return data;
    }

    public class UpdateProgressTask extends TimerTask {
        @Override
        public void run() {
            updateProgress();
        }

    }
}
