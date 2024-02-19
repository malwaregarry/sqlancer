package sqlancer.oracle.tlp;

import sqlancer.Randomly;
import sqlancer.common.ast.newast.Select;
import sqlancer.common.gen.TLPGenerator;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.oracle.TestOracle;
import sqlancer.oracle.TestOracleUtils;
import sqlancer.state.GlobalState;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TLPWhereOracle<U, G extends GlobalState<?, U, ?>> implements TestOracle<G> {

    private final G state;

    private final TLPGenerator<U> gen;
    private final ExpectedErrors errors;

    private String generatedQueryString;

    public TLPWhereOracle(G state, TLPGenerator<U> gen) {
        this.state = state;
        this.gen = gen;
        this.errors = new ExpectedErrors();
    }

    @Override
    public void check() throws SQLException {
        Select<U> select = gen.generateSelect();

        select.setFetchColumns(gen.generateFetchColumns());
        select.setJoinClauses(gen.getRandomJoinClauses());
        select.setFromTables(gen.getTableRefs());
        select.setWhereClause(null);

        String originalQueryString = select.asString();
        generatedQueryString = originalQueryString;

        boolean orderBy = Randomly.getBooleanWithSmallProbability();
        if (orderBy) {
            select.setOrderByExpressions(gen.generateOrderBys());
        }

        var predicates = TestOracleUtils.InitializeTernaryPredicateVariants(gen);
        select.setWhereClause(predicates.predicate);
        String firstQueryString = select.asString();
        select.setWhereClause(predicates.negatedPredicate);
        String secondQueryString = select.asString();
        select.setWhereClause(predicates.isNullPredicate);
        String thirdQueryString = select.asString();

        List<String> firstResultSet = TestOracleUtils.GetResultSetFirstColumnAsString(originalQueryString, errors, state);
        List<String> combinedString = new ArrayList<>();
        List<String> secondResultSet = TestOracleUtils.GetCombinedResultSet(firstQueryString, secondQueryString, thirdQueryString,
                combinedString, !orderBy, state, errors);

        TestOracleUtils.AssumeResultSetsAreEqual(firstResultSet, secondResultSet, originalQueryString, combinedString,
                state);
    }


    @Override
    public String getLastQueryString() {
        return generatedQueryString;
    }
}
