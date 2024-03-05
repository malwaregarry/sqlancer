package sqlancer.common.oracle;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import sqlancer.ComparatorHelper;
import sqlancer.IgnoreMeException;
import sqlancer.Randomly;
import sqlancer.SQLGlobalState;
import sqlancer.common.ast.newast.Expression;
import sqlancer.common.ast.newast.Join;
import sqlancer.common.ast.newast.Select;
import sqlancer.common.gen.TLPHavingGenerator;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.schema.AbstractSchema;
import sqlancer.common.schema.AbstractTable;
import sqlancer.common.schema.AbstractTableColumn;
import sqlancer.common.schema.AbstractTables;

public class TLPHavingOracle<J extends Join<E, T, C>, E extends Expression<C>, S extends AbstractSchema<?, T>, T extends AbstractTable<C, ?, ?>, C extends AbstractTableColumn<?, ?>, G extends SQLGlobalState<?, S>>
        implements TestOracle<G> {

    private final G state;

    private TLPHavingGenerator<J, E, T, C> gen;
    private final ExpectedErrors errors;

    private String generatedQueryString;

    public TLPHavingOracle(G state, TLPHavingGenerator<J, E, T, C> gen, ExpectedErrors expectedErrors) {
        this.state = state;
        this.gen = gen;
        this.errors = expectedErrors;
    }

    @Override
    public void check() throws SQLException {
        S s = state.getSchema();
        AbstractTables<T, C> targetTables = TestOracleUtils.getRandomTableNonEmptyTables(s);
        gen = gen.setTablesAndColumns(targetTables);

        Select<J, E, T, C> select = gen.generateSelect();

        boolean shouldCreateDummy = false;
        List<E> fetchColumns = gen.generateFetchColumns(shouldCreateDummy);

        select.setFetchColumns(fetchColumns);
        select.setJoinClauses(gen.getRandomJoinClauses());
        select.setFromList(gen.getTableRefs());
        select.setGroupByClause(fetchColumns);
        select.setHavingClause(null);

        String originalQueryString = select.asString();
        generatedQueryString = originalQueryString;
        List<String> firstResultSet = ComparatorHelper.getResultSetFirstColumnAsString(originalQueryString, errors,
                state);

        boolean orderBy = Randomly.getBooleanWithSmallProbability();
        if (orderBy) {
            select.setOrderByClauses(gen.generateOrderBys());
        }

        E predicate = gen.getHavingClause();
        select.setHavingClause(predicate);
        String firstQueryString = select.asString();
        select.setHavingClause(gen.negatePredicate(predicate));
        String secondQueryString = select.asString();
        select.setHavingClause(gen.isNull(predicate));
        String thirdQueryString = select.asString();
        String combinedString = TestOracleUtils.combineQueryStrings(" UNION ALL ", firstQueryString, secondQueryString,
                thirdQueryString);

        if (combinedString.contains("EXIST")) {
            throw new IgnoreMeException();
        }

        List<String> secondResultSet = ComparatorHelper.getResultSetFirstColumnAsString(combinedString, errors, state);
        if (state.getOptions().logEachSelect()) {
            state.getLogger().writeCurrent(originalQueryString);
            state.getLogger().writeCurrent(combinedString);
        }

        if (new HashSet<>(firstResultSet).size() != new HashSet<>(secondResultSet).size()) {
            throw new AssertionError(originalQueryString + ";\n" + combinedString + ";");
        }
    }

    @Override
    public String getLastQueryString() {
        return generatedQueryString;
    }
}
