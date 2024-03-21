package sqlancer.common.gen;

import sqlancer.common.ast.newast.Expression;

public interface PredicateGenerator<U> {
    /**
     * Generates a boolean predicate.
     *
     * @return an expression that can be used in a boolean context.
     */
    <E extends Expression<U>> E generatePredicate();

    /**
     * Negates a predicate (i.e., uses a NOT operator).
     *
     * @param predicate the boolean predicate.
     * @return the negated predicate.
     */
    <E extends Expression<U>> E negatePredicate(E predicate);

    /**
     * Checks if an expression evaluates to NULL (i.e., implements the IS NULL operator).
     *
     * @param expr the expression
     * @return an expression that checks whether the expression evaluates to NULL.
     */
    <E extends Expression<U>> E isNull(E expr);
}
