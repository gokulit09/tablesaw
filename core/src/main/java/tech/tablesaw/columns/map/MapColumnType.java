package tech.tablesaw.columns.map;

import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.ReadOptions;

public class MapColumnType extends AbstractColumnType {
    public static final int BYTE_SIZE = 4;
    private static MapColumnType INSTANCE;

    public static MapColumnType instance() {
        if (INSTANCE == null) {
            INSTANCE = new MapColumnType(4, "MAP", "MAP");
        }

        return INSTANCE;
    }

    protected MapColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    public Column<?> create(String name) {
        return null;
    }

    public AbstractColumnParser<?> customParser(ReadOptions options) {
        return null;
    }
}
