package tech.tablesaw.aggregate;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

public class CaseFunctionTest {
    private Table table;

    public CaseFunctionTest() {
    }

    @BeforeEach
    void setUp() throws Exception {
        this.table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
    }

    @Test
    void caseFunction_1() {
        new ArrayList(100);
        Table result = this.table.when(this.table.stringColumn("who").isEqualTo("fox"), "Y", "decision").otherwise(this.table.stringColumn("who"));
        Assertions.assertEquals(4, result.columnCount());
    }

    @Test
    void caseFunction_2() {
        Table result = this.table.when(this.table.stringColumn("who").isEqualTo("fox"), this.table.stringColumn("who"), "decision").otherwise(this.table.stringColumn("who"));
        Assertions.assertEquals(4, result.columnCount());
    }

    @Test
    void caseFunction_3() {
        Table result = this.table.when(this.table.stringColumn("who").isEqualTo("fox"), this.table.intColumn("approval"), "decision").otherwise(0);
        Assertions.assertEquals(4, result.columnCount());
    }

    @Test
    void caseFunction_String() {
        Table result = this.table.when(this.table.stringColumn("who").isEqualTo("fox"), "Y", "decision").otherwise("N");
        Assertions.assertEquals(4, result.columnCount());
    }

    @Test
    void caseFunction_Int() {
        Table result = this.table.when(this.table.stringColumn("who").isEqualTo("fox"), 1, "decision").otherwise(0);
        Assertions.assertEquals(4, result.columnCount());
    }

    @Test
    void caseFunctionMultipleWhen_Int() {
        Table result = this.table.when(this.table.stringColumn("who").isEqualTo("fox").and(this.table.intColumn("approval").isEqualTo(53.0D)), 1, "decision").otherwise(0);
        Assertions.assertEquals(4, result.columnCount());
    }
}
