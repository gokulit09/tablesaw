package tech.tablesaw.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.Column;

public class MapColumnTest {
    private Table table;
    private DoubleColumn x;
    private DoubleColumn y;
    private StringColumn product;
    private MapColumn column;
    private MapColumn ncolumn;

    public MapColumnTest() {
    }

    @BeforeEach
    public void testCreateEmptyColumn() {
        this.table = Table.create("Test");
        double[] doubleData = new double[]{0.397D, 0.157D, -0.083D, -0.243D, -0.323D, -0.243D, -0.083D, 0.077D, 0.347D};
        this.product = StringColumn.create("product", Arrays.asList("a", "b", "c", "a"));
        this.x = DoubleColumn.create("price", doubleData);
        this.y = DoubleColumn.create("close_price", doubleData);
        this.column = MapColumn.create("sample");
        this.ncolumn = MapColumn.create("sample");
        this.ncolumn.setField("market").setField("day1").put(this.x);
        this.ncolumn.getField("market").getField("day1").put(this.y);
        this.ncolumn.putColumn("market/day2", this.x);
        this.ncolumn.putColumn("market/day2", this.y);
        this.table.addColumns(new Column[]{this.x, this.ncolumn});
    }

    @Test
    public void putColumn() {
        List<String> pathList = new ArrayList();
        pathList.add("market/day1/price");
        pathList.add("market/day1/close_price");
        pathList.add("market/day2/price");
        pathList.add("market/day2/close_price");
        Assertions.assertTrue(pathList.containsAll(this.ncolumn.iteratePath()));
    }

    @Test
    public void getMapColumn() {
        MapColumn day_1_column = (MapColumn)this.ncolumn.getColumn("market/day1");
        List<String> pathList = new ArrayList();
        pathList.add("price");
        pathList.add("close_price");
        Assertions.assertTrue(pathList.containsAll(day_1_column.iteratePath()));
    }

    @Test
    void getFlattenColumn() {
        DoubleColumn doubleColumn = (DoubleColumn)this.ncolumn.getColumn("market/day1/price");
        Assertions.assertTrue(doubleColumn.size() == this.x.size());
    }

    @Test
    void ColumnNotFound() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MapColumn ncolumn_new = MapColumn.create("sample");
            ncolumn_new.putColumn("market/day2", this.x);
            ncolumn_new.putColumn("market/day2", this.y);
            this.ncolumn.putColumn("market/day3", ncolumn_new.getColumn("market/day3"));
        });
    }

    @Test
    void addMapColumn() {
        MapColumn ncolumn_new = MapColumn.create("sample");
        ncolumn_new.putColumn("market/day3", this.x);
        ncolumn_new.putColumn("market/day3", this.y);
        this.ncolumn.putColumn("market", ncolumn_new.getColumn("market/day3"));
        List<String> pathList = new ArrayList();
        pathList.add("market/day1/price");
        pathList.add("market/day1/close_price");
        pathList.add("market/day2/price");
        pathList.add("market/day2/close_price");
        pathList.add("market/day3/price");
        pathList.add("market/day3/close_price");
        Assertions.assertTrue(pathList.containsAll(this.ncolumn.iteratePath()));
    }

    @Test
    void getFromAnotherColumnThenPut() {
        MapColumn ncolumn_new = MapColumn.create("sample");
        ncolumn_new.putColumn("market", this.x);
        ncolumn_new.putColumn("market", this.y);
        this.ncolumn.putColumn("market/day3", ncolumn_new.getColumn("market/price"));
        this.ncolumn.putColumn("market/day3", ncolumn_new.getColumn("market/close_price"));
        List<String> pathList = new ArrayList();
        pathList.add("market/day1/price");
        pathList.add("market/day1/close_price");
        pathList.add("market/day2/price");
        pathList.add("market/day2/close_price");
        pathList.add("market/day3/price");
        pathList.add("market/day3/close_price");
        Assertions.assertTrue(pathList.containsAll(this.ncolumn.iteratePath()));
    }

    @Test
    public void putSinglePathColumn() {
        MapColumn ncolumn_new = MapColumn.create("sample");
        ncolumn_new.putColumn(this.x);
        ncolumn_new.putColumn(this.y);
        List<String> pathList = new ArrayList();
        pathList.add("price");
        pathList.add("close_price");
        Assertions.assertTrue(pathList.containsAll(ncolumn_new.iteratePath()));
    }

    @Test
    public void doubleWhereConditionWithMapColumn() {
        Table result = this.table.where(this.table.doubleColumn("sample/market/day1/price").isEqualTo(0.397D));
        Assertions.assertEquals(result.doubleColumn("sample/market/day2/close_price").size(), 1);
    }

    @Test
    public void stringWhereConditionWithMapColumn() {
        this.ncolumn.putColumn("market/day1", this.product);
        Table result = this.table.where(this.table.stringColumn("sample/market/day1/product").isEqualTo("a"));
        this.table.structure();
        Assertions.assertEquals(result.doubleColumn("sample/market/day2/close_price").size(), 2);
    }
}
