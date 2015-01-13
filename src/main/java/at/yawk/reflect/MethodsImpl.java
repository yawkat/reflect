package at.yawk.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * @author yawkat
 */
class MethodsImpl<T, R> extends MembersImpl<T, Method, MethodsImpl<T, R>> implements Methods<T, R> {
    public MethodsImpl(Class<?> declaring) {
        super(Cache.getMethods(declaring));
    }

    MethodsImpl() {}

    @Override
    protected MethodsImpl<T, R> createEmpty() {
        return new MethodsImpl<>();
    }

    @Override
    public void eachMethod(ReflectiveConsumer<Method> consumer) throws UncheckedReflectiveOperationException {
        for (Member member : matching) {
            try {
                consumer.consume((Method) member);
            } catch (ReflectiveOperationException e) {
                throw new UncheckedReflectiveOperationException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <SubR extends R> SubR invoke(Object... args) {
        return (SubR) invoke(handle, args);
    }

    @Override
    public void each(Consumer<R> consumer, Object... args) {
        for (Member member : matching) {
            R value = doInvoke((Method) member, handle, args);
            consumer.accept(value);
        }
    }

    private R invoke(T instance, Object... args) {
        switch (selectionMode) {
        case ALL:
            R returnValue = null;
            for (Member method : matching) {
                returnValue = doInvoke((Method) method, instance, args);
            }
            return returnValue;
        case ONLY:
            if (matchingLength > 1) {
                throw new IllegalStateException("Too many methods found: " + Arrays.toString(matching));
            }
            // same behaviour as first apart from this
        case FIRST:
            if (matchingLength < 1) { throw new NoSuchElementException("Method not found"); }
            return doInvoke((Method) matching[0], instance, args);
        default:
            throw new UnsupportedOperationException("Unsupported selection mode " + selectionMode);
        }
    }

    @SuppressWarnings("unchecked")
    private R doInvoke(Method method, T on, Object[] args) {
        try {
            return (R) method.invoke(on, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UncheckedReflectiveOperationException(e);
        }
    }
}
