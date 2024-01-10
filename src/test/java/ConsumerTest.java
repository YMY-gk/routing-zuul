

import java.util.Objects;


@FunctionalInterface
public interface ConsumerTest<Integer> {

    /**
     * Evaluates this PredicateTest on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the PredicateTest,
     * otherwise {@code false}
     */
    boolean test(int t);

    /**
     * Returns a composed PredicateTest that represents a short-circuiting logical
     * AND of this PredicateTest and another.  When evaluating the composed
     * PredicateTest, if this PredicateTest is {@code false}, then the {@code other}
     * PredicateTest is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either PredicateTest are relayed
     * to the caller; if evaluation of this PredicateTest throws an exception, the
     * {@code other} PredicateTest will not be evaluated.
     *
     * @param other a PredicateTest that will be logically-ANDed with this
     *              PredicateTest
     * @return a composed PredicateTest that represents the short-circuiting logical
     * AND of this PredicateTest and the {@code other} PredicateTest
     * @throws NullPointerException if other is null
     */
    default ConsumerTest<Integer> and(ConsumerTest<? super Integer> other) {
        Objects.requireNonNull(other);
        System.out.println("0000-------->");

        return (t) -> test(t) && other.test(t+100);
    }

    /**
     * Returns a PredicateTest that represents the logical negation of this
     * PredicateTest.
     *
     * @return a PredicateTest that represents the logical negation of this
     * PredicateTest
     */
    default ConsumerTest<Integer> negate() {
        return (t) -> !test(t);
    }

    /**
     * Returns a composed PredicateTest that represents a short-circuiting logical
     * OR of this PredicateTest and another.  When evaluating the composed
     * PredicateTest, if this PredicateTest is {@code true}, then the {@code other}
     * PredicateTest is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either PredicateTest are relayed
     * to the caller; if evaluation of this PredicateTest throws an exception, the
     * {@code other} PredicateTest will not be evaluated.
     *
     * @param other a PredicateTest that will be logically-ORed with this
     *              PredicateTest
     * @return a composed PredicateTest that represents the short-circuiting logical
     * OR of this PredicateTest and the {@code other} PredicateTest
     * @throws NullPointerException if other is null
     */
    default ConsumerTest<Integer> or(ConsumerTest<? super Integer> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
    }

    /**
     * Returns a PredicateTest that tests if two arguments are equal according
     * to {@link Objects#equals(Object, Object)}.
     *
     * @param <Integer> the type of arguments to the PredicateTest
     * @param targetRef the object reference with which to compare for equality,
     *               which may be {@code null}
     * @return a PredicateTest that tests if two arguments are equal according
     * to {@link Objects#equals(Object, Object)}
     */
    static <Integer> ConsumerTest<Integer> isEqual(Object targetRef) {
        return (null == targetRef)
                ? Objects::isNull
                : object -> targetRef.equals(object);
    }
}
