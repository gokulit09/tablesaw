package tech.tablesaw.api;

import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.map.MapColumnType;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.util.StringUtils;

public class MapColumn<K extends String, V extends Column> extends AbstractColumn<MapColumn<K, V>, MapColumn<K, V>> {
    private final HashMap<K, V> data = new HashMap();

    public MapColumn(String name, HashMap<K, V> data) {
        super(MapColumnType.instance(), name);
    }

    public static MapColumn create(String name) {
        return new MapColumn(name, new HashMap());
    }

    public K[] stringSplit(K k) {
        return (K[]) k.split("/");
    }

    public K pathString(K k) {
        String[] keyValue = this.stringSplit(k);
        return keyValue.length > 0 ? (K) StringUtils.join(Arrays.copyOfRange(keyValue, 0, keyValue.length - 1), '/') : null;
    }

    public MapColumn putColumn(K k, V v) {
        K[] path = this.stringSplit(k);
        this.putColumn(path, v);
        return this;
    }

    public MapColumn putColumn(V v) {
        this.put(v);
        return this;
    }

    public MapColumn putColumn(K[] k, V v) {
        this.setFields(k).put(v);
        return this;
    }

    public MapColumn setField(K k) {
        this.put((V) create(k));
        return (MapColumn)this.data.get(k);
    }

    public Set keys() {
        return this.data.keySet();
    }

    public MapColumn getField(K k) {
        return (MapColumn)this.data.get(k);
    }

    public V getColumn(K k) {
        K[] path = this.stringSplit(k);
        return this.getColumn(path);
    }

    public V getColumn(K[] k) {
        try {
            return this.getFields(k);
        } catch (Exception var3) {
            var3.printStackTrace();
            throw new IllegalArgumentException(String.format("Column path %s is not found", StringUtils.join(k, '/')));
        }
    }

    public V get(K k) {
        if (this.data.containsKey(k)) {
            return  this.data.get(k);
        } else {
            throw new IllegalArgumentException(String.format("Column %s is not present", k));
        }
    }

    public V put(V v) {
        return this.data.put((K) v.name(), v);
    }

    public List<K> iteratePath() {
        final List<K> iterateList = new ArrayList();
        Iterator var2 = this.data.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<K, V> entry = (Entry)var2.next();
            K path = (K) entry.getKey();
            if (entry.getValue() instanceof MapColumn) {
                class Iterate {
                    Iterate() {
                    }

                    K iterate(MapColumn<K, V> map, K path) {
                        Iterator var3 = map.data.entrySet().iterator();

                        while(var3.hasNext()) {
                            Entry<K, V> entry = (Entry)var3.next();
                            if (entry.getValue() instanceof MapColumn) {
                                this.iterate((MapColumn)entry.getValue(), (K) (path + "/" + entry.getKey()));
                            } else {
                                iterateList.add((K) (path + "/" + entry.getKey()));
                            }
                        }

                        return path;
                    }
                }

                (new Iterate()).iterate((MapColumn)entry.getValue(), path);
            } else {
                iterateList.add(path);
            }
        }

        return iterateList;
    }

    public Boolean isNestedMapColumn(K k) {
        return this.data.containsKey(k) && this.data.getOrDefault(k, (V) null) instanceof MapColumn ? true : false;
    }

    public MapColumn setFields(K... k) {
        return this.setOrGetField(this, 0, k);
    }

    public V getFields(K... k) {
        V map = (V) this;
        String[] var3 = k;
        int var4 = k.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            K key = (K) var3[var5];
            map = (V) ((MapColumn)map).get(key);
        }

        return (V) map;
    }

    private MapColumn setOrGetField(MapColumn map, Integer i, K[] k) {
        Integer size = k.length;
        if (k.length <= 0) {
            return map;
        } else {
            if (!map.data.containsKey(k[i]) || !map.isNestedMapColumn(k[i])) {
                map.setField(k[i]);
            }

            return size != i + 1 ? this.setOrGetField(map.getField(k[i]), i + 1, k) : map.getField(k[i]);
        }
    }

    public MapColumn<K, V> setMapRow(Selection rows, MapColumn<K, V> oldMapColumn) {
        oldMapColumn.iteratePath().forEach((columnName) -> {
            V oldColumn = oldMapColumn.getColumn(columnName);
            V column = this.getColumn(columnName);
            int r = 0;

            for(IntIterator var7 = rows.iterator(); var7.hasNext(); ++r) {
                int i = (Integer)var7.next();
                column.set(r, oldColumn, i);
            }

            this.putColumn(this.pathString(columnName), column);
        });
        return this;
    }

    public int size() {
        List<K> path = this.iteratePath();
        return path.isEmpty() ? 0 : this.getColumn((K) path.get(0)).size();
    }

    public Table summary() {
        return null;
    }

    public MapColumn<K, V>[] asObjectArray() {
        return new MapColumn[0];
    }

    public int countMissing() {
        return 0;
    }

    public String getString(int row) {
        return null;
    }

    public MapColumn<K, V> get(int row) {
        return null;
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

    public Column setMissing(int i) {
        return null;
    }

    public Column emptyCopy() {
        MapColumn empty = create(this.name());
        this.iteratePath().forEach((column) -> {
            String pathColumn = this.pathString(column);
            if (pathColumn == null) {
                empty.putColumn(this.getColumn(column).emptyCopy());
            } else {
                empty.putColumn(pathColumn, this.getColumn(column).emptyCopy());
            }

        });
        return empty;
    }

    public Column copy() {
        return null;
    }

    public Column emptyCopy(int rowSize) {
        MapColumn empty = create(this.name());
        this.iteratePath().forEach((column) -> {
            String pathColumn = this.pathString(column);
            if (pathColumn == null) {
                empty.putColumn(this.getColumn(column).emptyCopy(rowSize));
            } else {
                empty.putColumn(pathColumn, this.getColumn(column).emptyCopy(rowSize));
            }

        });
        return empty;
    }

    public Column set(int row, K columnName, V column, int sourceRow) {
        this.putColumn((V) this.getColumn(columnName).set(row, column, sourceRow));
        return this;
    }

    public Column lag(int n) {
        return null;
    }

    public Column appendCell(String stringValue) {
        return null;
    }

    public Column appendCell(String stringValue, AbstractColumnParser<?> parser) {
        return null;
    }

    public Column set(int row, MapColumn<K, V> value) {
        return null;
    }

    public Column set(int row, Column sourceColumn, int sourceRow) {
        return null;
    }

    public Column append(MapColumn<K, V> value) {
        return null;
    }

    public Column append(Column column) {
        return null;
    }

    public Column append(Column column, int row) {
        return null;
    }

    public Column appendObj(Object value) {
        return null;
    }

    public Column appendMissing() {
        return null;
    }

    public Column where(Selection selection) {
        MapColumn result = create(this.name());
        this.iteratePath().forEach((column) -> {
            String pathColumn = this.pathString(column);
            if (pathColumn == null) {
                result.putColumn(this.getColumn(column).where(selection));
            } else {
                result.putColumn(pathColumn, this.getColumn(column).where(selection));
            }

        });
        return result;
    }

    public Column removeMissing() {
        return null;
    }

    public Column unique() {
        return null;
    }

    public Iterator<MapColumn<K, V>> iterator() {
        return null;
    }

    public int compare(MapColumn<K, V> o1, MapColumn<K, V> o2) {
        return 0;
    }
}
