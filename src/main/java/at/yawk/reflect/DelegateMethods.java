package at.yawk.reflect;

import java.lang.reflect.Method;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
public class DelegateMethods<T, R> implements Methods<T, R> {
    private final Methods<T, R> handle;

    public DelegateMethods(Methods<T, R> handle) {
        this.handle = handle;
    }

    protected Methods<T, R> getHandle() {
        return handle;
    }

    protected Methods<T, R> wrap(Methods<T, R> methods) {
        return methods == handle ? this : new DelegateMethods<>(methods);
    }

    @Override
    public Methods<T, R> name(String name) {
        return wrap(handle.name(name));
    }

    @Override
    public Methods<T, R> modifier(int modifier) {
        return wrap(handle.modifier(modifier));
    }

    @Override
    public Methods<T, R> withoutModifier(int modifier) {
        return wrap(handle.withoutModifier(modifier));
    }

    @Override
    public Methods<T, R> match(Predicate<Method> predicate) {
        return wrap(handle.match(predicate));
    }

    @Override
    public Methods<T, R> mode(SelectionMode selectionMode) {
        return wrap(handle.mode(selectionMode));
    }

    @Override
    public Methods<T, R> first() {
        return wrap(handle.first());
    }

    @Override
    public Methods<T, R> all() {
        return wrap(handle.all());
    }

    @Override
    public Methods<T, R> only() {
        return wrap(handle.only());
    }

    @Override
    public Methods<T, R> finish() {
        return wrap(handle.finish());
    }

    @Override
    public Methods<T, R> statics() {
        return wrap(handle.statics());
    }

    @Override
    public Methods<T, R> on(T on) {
        return wrap(handle.on(on));
    }

    @Override
    public R invoke(Object... args) throws UncheckedReflectiveOperationException {
        return handle.invoke(args);
    }
}
