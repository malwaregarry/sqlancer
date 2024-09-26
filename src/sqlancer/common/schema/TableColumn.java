package sqlancer.common.schema;

public interface TableColumn<U> extends Comparable<TableColumn<U>> {
    String getName();

    String getFullQualifiedName();

    void setTable(Table<?> table);

    Table<?> getTable();

    U getType();

    @Override
    String toString();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    default int compareTo(TableColumn o) {
        return getName().compareTo(o.getName());
    }

}
