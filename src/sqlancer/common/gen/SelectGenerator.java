package sqlancer.common.gen;

import sqlancer.common.ast.newast.Expression;
import sqlancer.common.ast.newast.Join;
import sqlancer.common.ast.newast.Select;

import java.util.List;

public interface SelectGenerator<U> {

    <E extends Select<U>> E generateSelect();

    <E extends Expression<U>> List<E> generateFetchColumns();

    <E extends Expression<U>> List<E> generateOrderBys();

    <E extends Join<U>> List<E> getRandomJoinClauses();

    <E extends Expression<U>> List<E> getTableRefs();
}
