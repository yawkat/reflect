/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
public interface Fields<T, R> extends Members<T> {
    public static <T, R> Fields<T, R> ofType(Class<T> clazz) {
        return new FieldsImpl<>(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <R> Fields<?, R> of(Object obj) {
        return Fields.<Object, R>ofType((Class) obj.getClass()).on(obj);
    }

    @SuppressWarnings("unchecked")
    public static <R> Fields<?, R> of(Class<?> clazz) {
        return Fields.<Object, R>ofType((Class) clazz).statics();
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
     * mode(SelectionMode.ALL)
     */
    Fields<T, R> all();

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

    Fields<T, R> assignableTo(R value);

    <NR extends R> Fields<T, NR> assignableTo(Class<NR> type);

    /**
     * Get instance methods of the given object.
     */
    @Override
    Fields<T, R> on(T on);

    /**
     * Perform an action on each matched field (independent from SelectionMode).
     */
    void eachField(ReflectiveConsumer<Field> consumer) throws UncheckedReflectiveOperationException;

    /**
     * Invoke this method.
     */
    // using another type parameter here so we can don't have to state type params
    // explicitly on construction.
    <Return extends R> Return get() throws UncheckedReflectiveOperationException;

    void set(R value) throws UncheckedReflectiveOperationException;

    default <NewT> Methods<?, NewT> methods() {
        return Methods.of((Object) get());
    }

    default <NewT> Fields<?, NewT> fields() {
        return Fields.of((Object) get());
    }

    /**
     * Perform an action on each matched field value (independent from SelectionMode).
     */
    void each(Consumer<R> consumer);

    Field handle();
}
