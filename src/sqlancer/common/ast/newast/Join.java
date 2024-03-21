package sqlancer.common.ast.newast;

import sqlancer.common.schema.Table;

public interface Join<U> extends Expression<U> {

    enum JoinType {
        INNER, CROSS, OUTER, NATURAL, RIGHT, FULL;
    }

    Table<U> getTable();

    Expression<U> getOnClause();

    JoinType getType();

    void setOnClause(Expression<U> onClause);

    void setType(JoinType type);
}
