package tech.tablesaw.aggregate;

import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.ListTableColumn;
import tech.tablesaw.api.MapColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

public class ComputeFunctionTest {
    private Table table;

    public ComputeFunctionTest() {
    }

    @BeforeEach
    void setUp() throws Exception {
        this.table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
    }

    @Test
    void testDateMin() {
        StringColumn byColumn = this.table.dateColumn("date").yearQuarter();
        Table result = this.table.compute("approval", AggregateFunctions.mean).compute("date", AggregateFunctions.earliestDate).by(new CategoricalColumn[]{byColumn});
        Assertions.assertEquals(3, result.columnCount());
        Assertions.assertEquals(13, result.rowCount());
    }

    @Test
    void testInstantMinMax() {
        Instant i1 = Instant.ofEpochMilli(10000L);
        Instant i2 = Instant.ofEpochMilli(20000L);
        Instant i3 = Instant.ofEpochMilli(30000L);
        Instant i4 = null;
        InstantColumn ic = InstantColumn.create("instants", 5);
        Column icc = InstantColumn.create("instants", 5);
        ic.appendMissing();
        ic.append(i3);
        ic.append(i1);
        ic.append(i2);
        ic.appendMissing();
        ic.append((Instant)i4);
        Table test = Table.create("testInstantMath", new Column[]{ic});
        Table minI = test.compute("instants", AggregateFunctions.minInstant).apply();
        Table maxI = test.compute("instants", AggregateFunctions.maxInstant).apply();
        Assertions.assertEquals(i1, minI.get(0, 0));
        Assertions.assertEquals(i3, maxI.get(0, 0));
    }

    @Test
    void testComplexSummarizingWithNestedColumnList() {
        this.table.addColumns(new Column[]{this.table.numberColumn("approval").cube()});
        this.table.column(3).setName("cubed");
        StringColumn byColumn1 = this.table.stringColumn("who");
        IntColumn byColumn2 = this.table.dateColumn("date").year();
        Table result = this.table.compute("approval", AggregateFunctions.max).compute("cubed", AggregateFunctions.min).table_list("newList", new Column[]{byColumn2, byColumn1}).by(new CategoricalColumn[]{byColumn1});
        ListTableColumn byColumn3 = (ListTableColumn)result.column("newList");
        StringColumn byColumn12 = result.stringColumn("who");
        Table newResult_1 = result.compute("max [approval]", AggregateFunctions.max).table_list("rootList", new Column[]{byColumn3}).by(new CategoricalColumn[]{byColumn12});
        DoubleColumn dc = (DoubleColumn)((ListTableColumn)result.column("newList")).compute("date year", AggregateFunctions.sum);
        MapColumn ncolumn_new = MapColumn.create("sample");
        ncolumn_new.putColumn("path/newList", byColumn3);
        result.addColumns(new Column[]{ncolumn_new});
        StringColumn byColumn13 = result.stringColumn("who");
        Table newResult_2 = result.compute("max [approval]", AggregateFunctions.max).table_list("rootList", new Column[]{ncolumn_new, byColumn13}).by(new CategoricalColumn[]{byColumn13});
        result.print();
        Assertions.assertEquals(5, result.columnCount());
        Assertions.assertEquals("who", result.column(0).name());
        Assertions.assertEquals("max [approval]", result.column(1).name().toLowerCase());
    }
}
