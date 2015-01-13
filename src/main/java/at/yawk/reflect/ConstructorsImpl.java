package at.yawk.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * @author yawkat
 */
class ConstructorsImpl<T> extends MembersImpl<T, Constructor<T>, ConstructorsImpl<T>>
        implements Constructors<T> {
    public ConstructorsImpl(Class<?> declaring) {
        super(Cache.getConstructors(declaring));
    }

    ConstructorsImpl() {}

    @Override
    protected ConstructorsImpl<T> createEmpty() {
        return new ConstructorsImpl<>();
    }

    @Override
    public ConstructorsImpl<T> all() {
        return mode(SelectionMode.ALL);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void eachConstructor(ReflectiveConsumer<Constructor<T>> consumer)
            throws UncheckedReflectiveOperationException {
        for (Member member : matching) {
            try {
                consumer.consume((Constructor<T>) member);
            } catch (ReflectiveOperationException e) {
                throw new UncheckedReflectiveOperationException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T invoke(Object... args) {
        switch (selectionMode) {
        case ALL:
            T returnValue = null;
            for (Member constructor : matching) {
                returnValue = newInstance((Constructor<T>) constructor, args);
            }
            return returnValue;
        case ONLY:
            if (matchingLength > 1) {
                throw new IllegalStateException("Too many constructors found: " + Arrays.toString(matching));
            }
            // same behaviour as first apart from this
        case FIRST:
            if (matchingLength < 1) { throw new NoSuchElementException("Constructor not found"); }
            return newInstance((Constructor<T>) matching[0], args);
        default:
            throw new UnsupportedOperationException("Unsupported selection mode " + selectionMode);
        }
    }

    private T newInstance(Constructor<T> constructor, Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new UncheckedReflectiveOperationException(e);
        }
    }
}
