package tech.tablesaw.api;

import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.listtable.ListTableType;
import tech.tablesaw.selection.Selection;

public class ListTableColumn extends AbstractColumn<ListTableColumn, Table> {
    private final List<Table> data;

    public ListTableColumn(String name, List listTable) {
        super(ListTableType.instance(), name);
        this.data = listTable;
    }

    public static ListTableColumn create(String name, List<Table> listTable) {
        return new ListTableColumn(name, listTable);
    }

    public static ListTableColumn create(String name, int initialSize) {
        ListTableColumn column = new ListTableColumn(name, new ArrayList());

        for(int i = 0; i < initialSize; ++i) {
            column.appendMissing();
        }

        return column;
    }

    public String[] stringSplit(String k) {
        return k.split("/");
    }

    public ListTableColumn getPath(String path) {
        String[] listMapPaths = path.split("\\.");
        if (listMapPaths.length > 0) {
        }

        for(int i = 0; i < listMapPaths.length; ++i) {
            System.out.println(listMapPaths[i]);
        }

        return this;
    }

    public ListTableColumn getColumn(String name) {
        List<Table> tableList = new ArrayList();

        Table var4;
        for(Iterator var3 = this.data.iterator(); var3.hasNext(); var4 = (Table)var3.next()) {
        }

        return create(this.name(), tableList);
    }

    public ListTableColumn get(String name) {
        List<Table> tableList = new ArrayList();
        Iterator var3 = this.data.iterator();

        while(var3.hasNext()) {
            Table view = (Table)var3.next();
            tableList.add(view.select(new String[]{name}));
        }

        return create(this.name(), tableList);
    }

    public Column<?> compute(String columnName, AggregateFunction function) {
        ColumnType type = function.returnType();
        String colName = aggregateColumnName(columnName, function.functionName());
        Column resultColumn = type.create(colName);
        Iterator var6 = this.data.iterator();

        while(var6.hasNext()) {
            Table subTable = (Table)var6.next();
            Object result = function.summarize(subTable.column(columnName));
            if (result instanceof Number) {
                Number number = (Number)result;
                resultColumn.append(number.doubleValue());
            } else {
                resultColumn.append(result);
            }
        }

        return resultColumn;
    }

    public static String aggregateColumnName(String columnName, String functionName) {
        return String.format("%s [%s]", functionName, columnName);
    }

    public int size() {
        return this.data.size();
    }

    public Table summary() {
        return null;
    }

    public Table[] asObjectArray() {
        return new Table[0];
    }

    public int countMissing() {
        return 0;
    }

    public String getString(int row) {
        return null;
    }

    public Table get(int row) {
        return (Table)this.data.get(row);
    }

    public void clear() {
    }

    public void sortAscending() {
    }

    public void sortDescending() {
    }

    public boolean isEmpty() {
        return false;
    }

    public IntComparator rowComparator() {
        return null;
    }

    public Selection isMissing() {
        return null;
    }

    public Selection isNotMissing() {
        return null;
    }

    public int byteSize() {
        return 0;
    }

    public byte[] asBytes(int rowNumber) {
        return new byte[0];
    }

    public String getUnformattedString(int r) {
        return null;
    }

    public boolean isMissing(int rowNumber) {
        return false;
    }

    public Column<Table> setMissing(int i) {
        return null;
    }

    public Column<Table> emptyCopy() {
        return create(this.name(), new ArrayList());
    }

    public Column<Table> copy() {
        return null;
    }

    public Column<Table> emptyCopy(int rowSize) {
        return create(this.name(), rowSize);
    }

    public Column<Table> lag(int n) {
        return null;
    }

    public Column<Table> appendCell(String stringValue) {
        return null;
    }

    public Column<Table> appendCell(String stringValue, AbstractColumnParser<?> parser) {
        return null;
    }

    public Column<Table> set(int row, Table value) {
        return null;
    }

    public Column<Table> set(int row, Column<Table> sourceColumn, int sourceRow) {
        return null;
    }

    public Column<Table> append(Table value) {
        if (value == null) {
            this.appendMissing();
        } else {
            this.data.add(value);
        }

        return this;
    }

    public Column<Table> append(Column<Table> column) {
        return null;
    }

    public Column<Table> append(Column<Table> column, int row) {
        return null;
    }

    public Column<Table> appendObj(Object value) {
        if (value == null) {
            return this.appendMissing();
        } else if (!(value instanceof Table)) {
            throw new IllegalArgumentException("Cannot append " + value.getClass().getName() + " to BooleanColumn");
        } else {
            return this.append((Table)value);
        }
    }

    public Column<Table> appendMissing() {
        this.data.add((Table) null);
        return this;
    }

    public Column<Table> where(Selection selection) {
        return this.subset(selection.toArray());
    }

    public Column<Table> removeMissing() {
        return null;
    }

    public Column<Table> unique() {
        return null;
    }

    public Iterator<Table> iterator() {
        return null;
    }

    public int compare(Table o1, Table o2) {
        return 0;
    }
}
