package sqlancer.common.oracle;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import sqlancer.ComparatorHelper;
import sqlancer.IgnoreMeException;
import sqlancer.Randomly;
import sqlancer.SQLGlobalState;
import sqlancer.common.ast.newast.Aggregate;
import sqlancer.common.ast.newast.Expression;
import sqlancer.common.ast.newast.Select;
import sqlancer.common.gen.TLPAggregateGenerator;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.schema.AbstractSchema;
import sqlancer.common.schema.AbstractTable;
import sqlancer.common.schema.AbstractTableColumn;
import sqlancer.common.schema.AbstractTables;

public class TLPAggregateOracle<A extends Aggregate<E, C>, E extends Expression<C>, S extends AbstractSchema<?, T>, T extends AbstractTable<C, ?, ?>, C extends AbstractTableColumn<?, ?>, G extends SQLGlobalState<?, S>>
        implements TestOracle<G> {

    private final G state;

    private TLPAggregateGenerator<A, ?, E, T, C> gen;
    private final ExpectedErrors errors;
    private final boolean canOrderByInPartitionedQueries;

    private String generatedQueryString;

    public TLPAggregateOracle(G state, TLPAggregateGenerator<A, ?, E, T, C> gen, ExpectedErrors expectedErrors,
            boolean canOrderByInPartitionedQueries) {
        this.state = state;
        this.gen = gen;
        this.errors = expectedErrors;
        this.canOrderByInPartitionedQueries = canOrderByInPartitionedQueries;
    }

    @Override
    public void check() throws SQLException {
        S s = state.getSchema();
        AbstractTables<T, C> targetTables = TestOracleUtils.getRandomTableNonEmptyTables(s);
        gen = gen.setTablesAndColumns(targetTables);

        Select<?, E, T, C> select = gen.generateSelect();

        A aggregate = gen.generateAggregate();
        select.setFetchColumns(Arrays.asList(aggregate.asExpression()));

        List<E> from = gen.getTableRefs();
        select.setFromList(from);

        if (Randomly.getBoolean()) {
            select.setOrderByClauses(gen.generateOrderBys());
        }

        String originalQuery = select.asString();
        generatedQueryString = originalQuery;
        String firstResult = ComparatorHelper.runQuery(originalQuery, errors, state);

        E whereClause = gen.generateBooleanExpression();
        E negatedClause = gen.negatePredicate(whereClause);
        E notNullClause = gen.isNull(whereClause);

        Select<?, E, T, C> leftSelect = getSelect(aggregate, from, whereClause);
        Select<?, E, T, C> middleSelect = getSelect(aggregate, from, negatedClause);
        Select<?, E, T, C> rightSelect = getSelect(aggregate, from, notNullClause);

        String metamorphicQuery = aggregate.asAggregatedString(leftSelect.asString(), middleSelect.asString(),
                rightSelect.asString());

        String secondResult = ComparatorHelper.runQuery(metamorphicQuery, errors, state);

        String queryFormatString = "-- %s;\n-- result: %s";
        String firstQueryString = String.format(queryFormatString, originalQuery, firstResult);
        String secondQueryString = String.format(queryFormatString, metamorphicQuery, secondResult);
        state.getState().getLocalState().log(String.format("%s\n%s", firstQueryString, secondQueryString));

        if ((firstResult == null && secondResult != null
                || firstResult != null && !firstResult.contentEquals(secondResult))
                && !ComparatorHelper.isEqualDouble(firstResult, secondResult)) {

            if (secondResult != null && secondResult.contains("Inf")) {
                throw new IgnoreMeException(); // FIXME: average computation
            }

            String assertionMessage = String.format("the results mismatch!\n%s\n%s", firstQueryString,
                    secondQueryString);
            throw new AssertionError(assertionMessage);
        }
    }

    private Select<?, E, T, C> getSelect(A aggregate, List<E> from, E whereClause) {
        Select<?, E, T, C> leftSelect = gen.generateSelect();
        leftSelect.setFetchColumns(gen.aliasAggregates(List.of(aggregate)));
        leftSelect.setFromList(from);
        leftSelect.setWhereClause(whereClause);
        if (Randomly.getBooleanWithRatherLowProbability()) {
            leftSelect.setGroupByClause(gen.generateExpressions(Randomly.smallNumber() + 1));
        }
        if (canOrderByInPartitionedQueries && Randomly.getBoolean()) {
            leftSelect.setOrderByClauses(gen.generateOrderBys());
        }
        return leftSelect;
    }

    @Override
    public String getLastQueryString() {
        return generatedQueryString;
    }
}
