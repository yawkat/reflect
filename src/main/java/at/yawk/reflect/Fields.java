package at.yawk.reflect;

import java.lang.reflect.Field;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
public interface Fields<T, R> extends Members<T> {
    public static <T, R> Fields<T, R> of(Class<T> clazz) {
        return new FieldsImpl<>(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <R> Fields<?, R> ofInstance(Object obj) {
        return Fields.<Object, R>of((Class) obj.getClass()).on(obj);
    }

    @SuppressWarnings("unchecked")
    public static <R> Fields<?, R> ofStatic(Class<?> clazz) {
        return Fields.<Object, R>of((Class) clazz).statics();
    }

    /**
     * Only keep methods with the given name (case-sensitive).
     */
    @Override
    Fields<T, R> name(String name);

    /**
     * Remove methods without the given modifier or without all of the given modifiers.
     */
    @Override
    Fields<T, R> modifier(int modifier);

    /**
     * Remove methods with the given modifier or one of the given modifiers.
     */
    @Override
    Fields<T, R> withoutModifier(int modifier);

    /**
     * Filter the methods to match the given predicate.
     */
    Fields<T, R> match(Predicate<Field> predicate);

    /**
     * Set the mode by which to select fields in #get.
     */
    @Override
    Fields<T, R> mode(SelectionMode selectionMode);

    /**
     * mode(SelectionMode.FIRST)
     */
    @Override
    Fields<T, R> first();

    /**
     * mode(SelectionMode.ONLY)
     */
    @Override
    Fields<T, R> only();

    /**
     * Make this object immutable. Subsequent calls will yield a copy of this object and will not modify this object.
     */
    @Override
    Fields<T, R> finish();

    /**
     * Get static methods of this class.
     */
    @Override
    Fields<T, R> statics();

    /**
     * Get instance methods of the given object.
     */
    @Override
    Fields<T, R> on(T on);

    /**
     * Invoke this method.
     */
    R get() throws UncheckedReflectiveOperationException;

    default <NewT> Methods<?, NewT> methods() {
        return Methods.ofInstance(get());
    }

    default <NewT> Fields<?, NewT> fields() {
        return Fields.ofInstance(get());
    }
}
