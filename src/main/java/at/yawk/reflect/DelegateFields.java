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
public class DelegateFields<T, R> extends Delegate<T, Fields<T, R>> implements Fields<T, R> {
    public DelegateFields(Fields<T, R> handle) {
        super(handle);
    }

    @Override
    protected Fields<T, R> wrapOther(Fields<T, R> fields) {
        return new DelegateFields<>(fields);
    }

    @Override
    public Fields<T, R> match(Predicate<Field> predicate) {
        return wrap(handle.match(predicate));
    }

    @Override
    public Fields<T, R> all() {
        return wrap(handle.all());
    }

    @Override
    public Fields<T, R> assignableTo(R value) {
        return wrap(handle.assignableTo(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <NR extends R> Fields<T, NR> assignableTo(Class<NR> type) {
        return (Fields<T, NR>) wrap((Fields<T, R>) handle.assignableTo(type));
    }

    @Override
    public void eachField(ReflectiveConsumer<Field> consumer) throws UncheckedReflectiveOperationException {
        handle.eachField(consumer);
    }

    @Override
    public <Return extends R> Return get() throws UncheckedReflectiveOperationException {
        return handle.get();
    }

    @Override
    public void set(R value) throws UncheckedReflectiveOperationException {
        handle.set(value);
    }

    @Override
    public <NewT> Methods<?, NewT> methods() {
        return wrapMethods(get());
    }

    @Override
    public <NewT> Fields<?, NewT> fields() {
        return wrapFields(get());
    }

    @Override
    public void each(Consumer<R> consumer) {
        handle.each(consumer);
    }

    @Override
    public Field handle() {
        return handle.handle();
    }
}
