package tech.tablesaw.aggregate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.ListTableColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;

public class Computation {
    private final Table original;
    private Table temp;
    private ArrayListMultimap<String, AggregateFunction<?, ?>> reductionMultimap;
    private HashMap<String, Column<?>[]> tableList = new HashMap();

    public Computation(Table sourceTable, String columnName, AggregateFunction function) {
        Table tempTable = Table.create(sourceTable.name());
        if (!tempTable.containsColumn(sourceTable.column(columnName))) {
            tempTable.addColumns(new Column[]{sourceTable.column(columnName)});
        }

        this.original = sourceTable;
        this.temp = tempTable;
        this.reductionMultimap = ArrayListMultimap.create();
        this.reductionMultimap.put(columnName, function);
    }

    public Computation compute(Column column, AggregateFunction<?, ?> function) {
        if (!this.temp.containsColumn(column)) {
            this.temp.addColumns(new Column[]{column});
        }

        this.reductionMultimap.put(column.name(), function);
        return this;
    }

    public Computation compute(String columnName, AggregateFunction<?, ?> function) {
        if (!this.temp.containsColumn(this.original.column(columnName))) {
            this.temp.addColumns(new Column[]{this.original.column(columnName)});
        }

        this.reductionMultimap.put(columnName, function);
        return this;
    }

    public Table by(String... columnNames) {
        String[] var2 = columnNames;
        int var3 = columnNames.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String columnName = var2[var4];
            if (this.tableDoesNotContain(columnName, this.temp)) {
                this.temp.addColumns(new Column[]{this.original.column(columnName)});
            }
        }

        TableSliceGroup group = StandardTableSliceGroup.create(this.temp, columnNames);
        return this.aggregate(group);
    }

    public Table by(CategoricalColumn<?>... columns) {
        CategoricalColumn[] var2 = columns;
        int var3 = columns.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Column<?> c = var2[var4];
            if (!this.temp.containsColumn(c)) {
                this.temp.addColumns(new Column[]{c});
            }
        }

        TableSliceGroup group = StandardTableSliceGroup.create(this.temp, columns);
        return this.aggregate(group);
    }

    private Table aggregate(TableSliceGroup group) {
        List<Table> results = new ArrayList();
        Iterator var3 = this.reductionMultimap.keys().iterator();

        while(var3.hasNext()) {
            String name = (String)var3.next();
            List<AggregateFunction<?, ?>> reductions = this.reductionMultimap.get(name);
            results.add(group.aggregate(name, (AggregateFunction[])reductions.toArray(new AggregateFunction[0])));
        }

        if (this.tableList.size() > 0) {
            Table listTable = Table.create("ListTable");
            Iterator var10 = this.tableList.entrySet().iterator();

            while(var10.hasNext()) {
                Entry<String, Column<?>[]> entry = (Entry)var10.next();
                Column<?>[] columns = (Column[])entry.getValue();
                String[] columnNames = new String[columns.length];

                for(int i = 0; i < columns.length; ++i) {
                    columnNames[i] = columns[i].name();
                }

                ListTableColumn c = ListTableColumn.create((String)entry.getKey(), group.asTableList(columnNames));
                listTable.addColumns(new Column[]{c});
            }

            results.add(listTable);
        }

        return this.combineTables(results);
    }

    public Computation table_list(String columnName, String... columnNames) {
        Column<?>[] columns = new Column[columnNames.length];

        for(int i = 0; i < columnNames.length; ++i) {
            columns[i] = this.original.column(columnNames[i]);
        }

        return this.table_list(columnName, columns);
    }

    public Computation table_list(String columnName, Column<?>... columns) {
        this.tableList.put(columnName, columns);
        Iterator var3 = this.tableList.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<String, Column<?>[]> entry = (Entry)var3.next();
            Column<?>[] column = (Column[])entry.getValue();

            for(int i = 0; i < ((Column[])entry.getValue()).length; ++i) {
                if (!this.temp.containsColumn(column[i])) {
                    this.temp.addColumns(new Column[]{column[i]});
                }
            }
        }

        return this;
    }

    private Table combineTables(List<Table> tables) {
        Preconditions.checkArgument(!tables.isEmpty());
        Table result = (Table)tables.get(0);

        for(int i = 1; i < tables.size(); ++i) {
            Table table = (Table)tables.get(i);
            Iterator var5 = table.columns().iterator();

            while(var5.hasNext()) {
                Column<?> column = (Column)var5.next();
                if (this.tableDoesNotContain(column.name(), result)) {
                    result.addColumns(new Column[]{column});
                }
            }
        }

        return result;
    }

    private boolean tableDoesNotContain(String columnName, Table table) {
        List<String> upperCase = (List)table.columnNames().stream().map(String::toUpperCase).collect(Collectors.toList());
        return !upperCase.contains(columnName.toUpperCase());
    }

    public Table apply() {
        List<Table> results = new ArrayList();
        Iterator var2 = this.reductionMultimap.keys().iterator();

        while(var2.hasNext()) {
            String name = (String)var2.next();
            List<AggregateFunction<?, ?>> reductions = this.reductionMultimap.get(name);
            Table table = TableSliceGroup.summaryTableName(this.temp);

            Column newColumn;
            for(Iterator var6 = reductions.iterator(); var6.hasNext(); table.addColumns(new Column[]{newColumn})) {
                AggregateFunction function = (AggregateFunction)var6.next();
                Column column = this.temp.column(name);
                Object result = function.summarize(column);
                ColumnType type = function.returnType();
                newColumn = type.create(TableSliceGroup.aggregateColumnName(name, function.functionName()));
                if (result instanceof Number) {
                    Number number = (Number)result;
                    newColumn.append(number.doubleValue());
                } else {
                    newColumn.append(result);
                }
            }

            results.add(table);
        }

        return this.combineTables(results);
    }
}
