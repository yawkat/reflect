/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
public interface Methods<T, R> extends Members<T> {
    public static <T, R> Methods<T, R> ofType(Class<T> clazz) {
        return new MethodsImpl<>(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <R> Methods<?, R> of(Object obj) {
        return Methods.<Object, R>ofType((Class) obj.getClass()).on(obj);
    }

    @SuppressWarnings("unchecked")
    public static <R> Methods<?, R> of(Class<?> clazz) {
        return Methods.<Object, R>ofType((Class) clazz).statics();
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
     * Perform an action on each matched method (independent from SelectionMode).
     */
    void eachMethod(ReflectiveConsumer<Method> consumer) throws UncheckedReflectiveOperationException;

    /**
     * Invoke this method.
     */
    // using another type parameter here so we can don't have to state type params
    // explicitly on construction.
    <Return extends R> Return invoke(Object... args) throws UncheckedReflectiveOperationException;

    default <NewT> Methods<?, NewT> methods(Object... args) {
        return Methods.of((Object) invoke(args));
    }

    default <NewT> Fields<?, NewT> fields(Object... args) {
        return Fields.of((Object) invoke(args));
    }

    /**
     * Perform an action on each methods matched return value after matching it with the given args (independent from
     * SelectionMode).
     */
    void each(Consumer<R> consumer, Object... args);

    Method handle();
}
