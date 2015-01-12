package at.yawk.reflect;

import java.lang.reflect.Field;
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
}
