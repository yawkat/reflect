package at.yawk.reflect;

import java.lang.reflect.Constructor;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
public interface Constructors<T> extends Members<T> {
    public static <T> Constructors<T> of(Class<T> clazz) {
        return new ConstructorsImpl<>(clazz);
    }

    /**
     * Remove methods without the given modifier or without all of the given modifiers.
     */
    @Override
    Constructors<T> modifier(int modifier);

    /**
     * Remove methods with the given modifier or one of the given modifiers.
     */
    @Override
    Constructors<T> withoutModifier(int modifier);

    /**
     * Filter the methods to match the given predicate.
     */
    Constructors<T> match(Predicate<Constructor<T>> predicate);

    /**
     * Set the mode by which to select methods in #invoke.
     */
    @Override
    Constructors<T> mode(SelectionMode selectionMode);

    /**
     * mode(SelectionMode.FIRST)
     */
    @Override
    Constructors<T> first();

    /**
     * mode(SelectionMode.ALL)
     */
    Constructors<T> all();

    /**
     * mode(SelectionMode.ONLY)
     */
    @Override
    Constructors<T> only();

    /**
     * Make this object immutable. Subsequent calls will yield a copy of this object and will not modify this object.
     */
    @Override
    Constructors<T> finish();

    /**
     * Perform an action on each matched constructor (independent from SelectionMode).
     */
    void eachConstructor(ReflectiveConsumer<Constructor<T>> consumer) throws UncheckedReflectiveOperationException;

    /**
     * Invoke this constructor.
     */
    T invoke(Object... args) throws UncheckedReflectiveOperationException;

    default <NewT> Methods<?, NewT> methods(Object... args) {
        return Methods.of(invoke(args));
    }

    default <NewT> Fields<?, NewT> fields(Object... args) {
        return Fields.of(invoke(args));
    }
}
