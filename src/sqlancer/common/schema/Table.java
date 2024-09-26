package sqlancer.common.schema;

import java.util.List;
import java.util.function.Predicate;

public interface Table<C extends TableColumn<?>> extends Comparable<Table<C>> {

    String getName();

    List<? extends TableIndex> getIndexes();

    List<C> getColumns();

    String getColumnsAsString();

    C getRandomColumn();

    C getRandomColumnOrBailout(Predicate<C> predicate);

    boolean hasIndexes();

    TableIndex getRandomIndex();

    List<C> getRandomNonEmptyColumnSubset();

    List<C> getRandomNonEmptyColumnSubset(int size);

    boolean isView();

    String getFreeColumnName();

    void recomputeCount();

    // long getNrRows(G globalState);

    @Override
    String toString();

    @Override
    default int compareTo(Table o) {
        return o.getName().compareTo(getName());
    }
}
