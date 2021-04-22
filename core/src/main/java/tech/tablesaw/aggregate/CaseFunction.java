package tech.tablesaw.aggregate;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

public class CaseFunction {
    private final List<Selection> rows = new ArrayList();
    private final List<Column> whenMatch = new ArrayList();
    private final List<Boolean> isColumnWhenMatch = new ArrayList();
    private final Column result;
    private Column whenNotMatch;
    private Boolean isColumnWhenNotMatch;
    private final String columnName;
    private final Table sourceTable;
    private Table resultTable;

    public CaseFunction(Table sourceTable, Selection rows, Object whenMatch, String columnName) {
        this.rows.add(rows);
        this.columnName = columnName;
        this.sourceTable = sourceTable;
        if (whenMatch instanceof Column) {
            this.whenMatch.add((Column)whenMatch);
            this.isColumnWhenMatch.add(true);
            this.result = this.parse((Column)whenMatch, columnName, this.sourceTable.rowCount());
        } else {
            this.whenMatch.add(this.parse(whenMatch, whenMatch.toString()).appendObj(whenMatch));
            this.isColumnWhenMatch.add(false);
            this.result = this.parse(whenMatch, columnName, this.sourceTable.rowCount());
        }

    }

    public CaseFunction when(Selection rows, Object whenMatch) {
        this.rows.add(rows);
        if (whenMatch instanceof Column) {
            this.whenMatch.add((Column)whenMatch);
            this.isColumnWhenMatch.add(true);
        } else {
            this.whenMatch.add(this.parse(whenMatch, whenMatch.toString()).appendObj(whenMatch));
            this.isColumnWhenMatch.add(false);
        }

        return this;
    }

    public Table otherwise(Object whenNotMatch) {
        if (whenNotMatch instanceof Column) {
            this.isColumnWhenNotMatch = true;
            this.whenNotMatch = this.parse((Column)whenNotMatch, this.columnName, this.sourceTable.rowCount());
        } else {
            this.whenNotMatch = this.parse(whenNotMatch, whenNotMatch.toString()).appendObj(whenNotMatch);
            this.isColumnWhenNotMatch = false;
        }

        Table copy = Table.create(this.sourceTable.name());
        Iterator var3 = this.sourceTable.columns().iterator();

        while(var3.hasNext()) {
            Column<?> column = (Column)var3.next();
            copy.addColumns(new Column[]{column});
        }

        this.apply();
        return copy.addColumns(new Column[]{this.result});
    }

    private Column parse(Object obj, String columnName, int size) {
        Object empty;
        if (obj instanceof String) {
            empty = StringColumn.create(columnName, size);
        } else if (obj instanceof Integer) {
            empty = IntColumn.create(columnName, size);
        } else if (obj instanceof Long) {
            empty = LongColumn.create(columnName, size);
        } else {
            if (!(obj instanceof Double)) {
                throw new IllegalArgumentException(obj.getClass().getName() + " is unsupported type");
            }

            empty = DoubleColumn.create(columnName, size);
        }

        return (Column)empty;
    }

    private Column parse(Object obj, String columnName) {
        Object empty;
        if (obj instanceof String) {
            empty = StringColumn.create(columnName);
        } else if (obj instanceof Integer) {
            empty = IntColumn.create(columnName);
        } else if (obj instanceof Long) {
            empty = LongColumn.create(columnName);
        } else {
            if (!(obj instanceof Double)) {
                throw new IllegalArgumentException(obj.getClass().getName() + " is unsupported type");
            }

            empty = DoubleColumn.create(columnName);
        }

        return (Column)empty;
    }

    private Column<?> parse(Column column, String columnName, int size) {
        Object emptyColumn;
        if (column instanceof DoubleColumn) {
            emptyColumn = DoubleColumn.create(columnName, size);
        } else if (column instanceof IntColumn) {
            emptyColumn = IntColumn.create(columnName, size);
        } else if (column instanceof ShortColumn) {
            emptyColumn = IntColumn.create(columnName, size);
        } else if (column instanceof LongColumn) {
            emptyColumn = ShortColumn.create(columnName, size);
        } else if (column instanceof FloatColumn) {
            emptyColumn = FloatColumn.create(columnName, size);
        } else if (column instanceof BooleanColumn) {
            emptyColumn = BooleanColumn.create(columnName, size);
        } else if (column instanceof StringColumn) {
            emptyColumn = StringColumn.create(columnName, size);
        } else if (column instanceof TextColumn) {
            emptyColumn = TextColumn.create(columnName, size);
        } else if (column instanceof DateColumn) {
            emptyColumn = DateColumn.create(columnName, size);
        } else if (column instanceof DateTimeColumn) {
            emptyColumn = DateTimeColumn.create(columnName, size);
        } else if (column instanceof InstantColumn) {
            emptyColumn = InstantColumn.create(columnName, size);
        } else {
            if (!(column instanceof TimeColumn)) {
                throw new IllegalArgumentException(column.getClass().getName() + " is unsupported type");
            }

            emptyColumn = TimeColumn.create(columnName, size);
        }

        return (Column)emptyColumn;
    }

    private void set(int row, Column value, int sourceRow, Boolean isColumn) {
        if (isColumn) {
            this.result.set(row, value, sourceRow);
        } else {
            this.result.set(row, value, 0);
        }

    }

    private void apply() {
        for(int i = 0; i < this.sourceTable.rowCount(); ++i) {
            Boolean conditionNotMatched = true;

            for(int j = 0; j < this.rows.size(); ++j) {
                if (((Selection)this.rows.get(j)).contains(i)) {
                    this.set(i, (Column)this.whenMatch.get(j), i, (Boolean)this.isColumnWhenMatch.get(j));
                    conditionNotMatched = false;
                    break;
                }
            }

            if (conditionNotMatched) {
                this.set(i, this.whenNotMatch, i, this.isColumnWhenNotMatch);
            }
        }

    }
}
