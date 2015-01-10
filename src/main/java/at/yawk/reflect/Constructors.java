package at.yawk.reflect;

import java.lang.reflect.Constructor;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
public interface Constructors<T> extends Members<T, T> {
    public static <T> Constructors<T> of(Class<T> clazz) {
        return new ConstructorsImpl<>(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructors<T> ofInstance(T obj) {
        return Constructors.<T>of((Class) obj.getClass()).on(obj);
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructors<T> ofStatic(Class<T> clazz) {
        return Constructors.<T>of(clazz).statics();
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
     * Get static methods of this class.
     */
    @Override
    Constructors<T> statics();

    /**
     * Get instance methods of the given object.
     */
    @Override
    Constructors<T> on(T on);

    /**
     * Invoke this method.
     */
    T invoke(Object... args) throws UncheckedReflectiveOperationException;

    default <NewT> Methods<T, NewT> methods(Object... args) {
        return Methods.ofInstance(invoke(args));
    }

    default <NewT> Fields<T, NewT> fields(Object... args) {
        return Fields.ofInstance(invoke(args));
    }
}
