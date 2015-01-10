package at.yawk.reflect;

import java.lang.reflect.Method;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
public interface Methods<T, R> extends Members<T, R> {
    public static <T, R> Methods<T, R> of(Class<T> clazz) {
        return new MethodsImpl<>(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Methods<T, R> ofInstance(T obj) {
        return Methods.<T, R>of((Class) obj.getClass()).on(obj);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Methods<T, R> ofStatic(Class<T> clazz) {
        return Methods.<T, R>of(clazz).statics();
    }

    /**
     * Only keep methods with the given name (case-sensitive).
     */
    @Override
    Methods<T, R> name(String name);

    /**
     * Remove methods without the given modifier or without all of the given modifiers.
     */
    @Override
    Methods<T, R> modifier(int modifier);

    /**
     * Remove methods with the given modifier or one of the given modifiers.
     */
    @Override
    Methods<T, R> withoutModifier(int modifier);

    /**
     * Filter the methods to match the given predicate.
     */
    Methods<T, R> match(Predicate<Method> predicate);

    /**
     * Set the mode by which to select methods in #invoke.
     */
    @Override
    Methods<T, R> mode(SelectionMode selectionMode);

    /**
     * mode(SelectionMode.FIRST)
     */
    @Override
    Methods<T, R> first();

    /**
     * mode(SelectionMode.ALL)
     */
    Methods<T, R> all();

    /**
     * mode(SelectionMode.ONLY)
     */
    @Override
    Methods<T, R> only();

    /**
     * Make this object immutable. Subsequent calls will yield a copy of this object and will not modify this object.
     */
    @Override
    Methods<T, R> finish();

    /**
     * Get static methods of this class.
     */
    @Override
    Methods<T, R> statics();

    /**
     * Get instance methods of the given object.
     */
    @Override
    Methods<T, R> on(T on);

    /**
     * Invoke this method.
     */
    R invoke(Object... args) throws UncheckedReflectiveOperationException;

    default <NewT> Methods<R, NewT> methods(Object... args) {
        return Methods.ofInstance(invoke(args));
    }

    default <NewT> Fields<R, NewT> fields(Object... args) {
        return Fields.ofInstance(invoke(args));
    }
}
