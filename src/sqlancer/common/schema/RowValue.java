package sqlancer.common.schema;

import java.util.List;
import java.util.Map;

public interface RowValue<U, O> {

    TableGroup getTable();

    Map<TableColumn<U>, O> getValues();

    String getRowValuesAsString();

    String getRowValuesAsString(List<TableColumn<U>> columnsToCheck);

    String asStringGroupedByTables();
}
