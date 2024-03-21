package sqlancer.common.ast.newast;

import sqlancer.common.schema.TableColumn;

public interface Expression<U> {
    Expression<U> ofColumnName(TableColumn<U> column, Constant<U> value);

    String asString();
}
