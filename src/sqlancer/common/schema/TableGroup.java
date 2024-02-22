package sqlancer.common.schema;

import java.util.List;
import java.util.function.Function;

public interface TableGroup<T extends Table<?>, C extends TableColumn<?>> {
    String tableNamesAsString();

    List<T> getTables();

    List<C> getColumns();

    String columnNamesAsString(Function<C, String> function);
}
