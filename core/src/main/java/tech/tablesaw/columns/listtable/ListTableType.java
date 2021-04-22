package tech.tablesaw.columns.listtable;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.ReadOptions;

public class ListTableType extends AbstractColumnType {
    public static final int BYTE_SIZE = 4;
    private static ListTableType INSTANCE;

    public static ListTableType instance() {
        if (INSTANCE == null) {
            INSTANCE = new ListTableType(4, "LIST_TABLE", "LIST_TABLE");
        }

        return INSTANCE;
    }

    protected ListTableType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    public Column<?> create(String name) {
        return null;
    }

    public AbstractColumnParser<?> customParser(ReadOptions options) {
        return null;
    }

    public static Table missingValueIndicator() {
        return null;
    }
}

