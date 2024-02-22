package sqlancer.common.schema;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Schema<T extends Table<?>> {
    String toString();

    T getRandomTable();

    T getRandomTableOrBailout();

    T getRandomTable(Predicate<T> predicate);

    T getRandomTableOrBailout(Function<T, Boolean> f);

    List<T> getDatabaseTables();

    List<T> getTables(Predicate<T> predicate);

    List<T> getDatabaseTablesRandomSubsetNotEmpty();

    Table getDatabaseTable(String name);

    List<T> getViews();

    List<T> getDatabaseTablesWithoutViews();

    T getRandomViewOrBailout();

    T getRandomTableNoViewOrBailout();

    String getFreeIndexName();

    String getFreeTableName();

    String getFreeViewName();

    // boolean containsTableWithZeroRows(G globalState);

    // TableGroup getRandomTableNonEmptyTables();

}
