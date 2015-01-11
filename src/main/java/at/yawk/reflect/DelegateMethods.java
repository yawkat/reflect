package at.yawk.reflect;

import java.lang.reflect.Method;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
public class DelegateMethods<T, R> extends Delegate<T, Methods<T, R>> implements Methods<T, R> {
    public DelegateMethods(Methods<T, R> handle) {
        super(handle);
    }

    protected DelegateMethods<T, R> wrapOther(Methods<T, R> methods) {
        return new DelegateMethods<>(methods);
    }

    @Override
    public Methods<T, R> match(Predicate<Method> predicate) {
        return wrap(handle.match(predicate));
    }

    @Override
    public Methods<T, R> all() {
        return wrap(handle.all());
    }

    @Override
    public <Return extends R> Return invoke(Object... args) throws UncheckedReflectiveOperationException {
        return handle.invoke(args);
    }

    @Override
    public <NewT> Methods<?, NewT> methods(Object... args) {
        return wrapMethods(invoke(args));
    }

    @Override
    public <NewT> Fields<?, NewT> fields(Object... args) {
        return wrapFields(invoke(args));
    }
}
