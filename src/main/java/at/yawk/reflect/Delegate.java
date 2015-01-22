/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

/**
 * @author yawkat
 */
@SuppressWarnings("unchecked")
public abstract class Delegate<T, M extends Members<T>> implements Members<T> {
    final M handle;

    public Delegate(M handle) {
        this.handle = handle;
    }

    protected final M getHandle() {
        return handle;
    }

    /**
     * Get the fields of an arbitrary object.
     *
     * @param fields The Fields instance our handle provided.
     */
    protected <NR> Fields<?, NR> wrap(Fields<?, NR> fields, Object o) {
        return new DelegateFields<>(fields);
    }

    /**
     * Get the methods of an arbitrary object.
     *
     * @param methods The Methods instance our handle provided.
     */
    protected <NR> Methods<?, NR> wrap(Methods<?, NR> methods, Object o) {
        return new DelegateMethods<>(methods);
    }

    /**
     * Wrap a handle of the same type as our current handle.
     */
    final M wrap(M other) {
        return other == handle ? (M) this : wrapOther(other);
    }

    /**
     * Wrap a handle of the same type as this Delegate instance wraps.
     */
    protected abstract M wrapOther(M other);

    @Override
    public M name(String name) {
        return wrap((M) handle.name(name));
    }

    @Override
    public M modifier(int modifier) {
        return wrap((M) handle.modifier(modifier));
    }

    @Override
    public M withoutModifier(int modifier) {
        return wrap((M) handle.withoutModifier(modifier));
    }

    @Override
    public M mode(SelectionMode selectionMode) {
        return wrap((M) handle.mode(selectionMode));
    }

    @Override
    public M first() {
        return wrap((M) handle.first());
    }

    @Override
    public M only() {
        return wrap((M) handle.only());
    }

    @Override
    public M finish() {
        return wrap((M) handle.finish());
    }

    @Override
    public M statics() {
        return wrap((M) handle.statics());
    }

    @Override
    public M on(T on) {
        return wrap((M) handle.on(on));
    }

    final <NewT> Fields<?, NewT> wrapFields(Object o) {
        Fields<?, NewT> w;
        if (handle instanceof Delegate) {
            w = ((Delegate<T, M>) handle).wrapFields(o);
        } else {
            w = Fields.of(o);
        }
        return wrap(w, o);
    }

    final <NewT> Methods<?, NewT> wrapMethods(Object o) {
        Methods<?, NewT> w;
        if (handle instanceof Delegate) {
            w = ((Delegate<T, M>) handle).wrapMethods(o);
        } else {
            w = Methods.of(o);
        }
        return wrap(w, o);
    }
}
