package sqlancer.common.ast.newast;


import java.util.List;

public interface Select<U> extends Expression<U> {
    enum SelectType {
        DISTINCT, ALL
    }

    void setSelectType(SelectType fromOptions);

    void setFromTables(List<Expression<U>> fromTables);

    SelectType getFromOptions();

    void setFromOptions(SelectType fromOptions);

    List<Expression<U>> getFromList();

    void setFromList(List<Expression<U>> fromList);

    Expression<U> getWhereClause();

    void setWhereClause(Expression<U> whereClause);

    void setGroupByClause(List<Expression<U>> groupByClause);

    List<Expression<U>> getGroupByClause();

    void setLimitClause(Expression<U> limitClause);

    Expression<U> getLimitClause();

    List<Expression<U>> getOrderByClause();

    void setOrderByExpressions(List<Expression<U>> orderBy);

    void setOffsetClause(Expression<U> offsetClause);

    Expression<U> getOffsetClause();

    void setFetchColumns(List<Expression<U>> fetchColumns);

    List<Expression<U>> getFetchColumns();

    void setJoinClauses(List<Join<U>> joinStatements);

    List<Join<U>> getJoinClauses();

    void setHavingClause(Expression<U> havingClause);

    Expression<U> getHavingClause();
}
