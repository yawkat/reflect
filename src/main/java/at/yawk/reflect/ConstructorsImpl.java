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
        super(Cache.getMethods(declaring));
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
    public T invoke(Object... args) {
        switch (selectionMode) {
        case ALL:
            T returnValue = null;
            for (Member constructor : matching) {
                returnValue = ((Constructors<T>) constructor).invoke(args);
            }
            return returnValue;
        case ONLY:
            if (matchingLength > 1) {
                throw new IllegalStateException("Too many constructors found: " + Arrays.toString(matching));
            }
            // same behaviour as first apart from this
        case FIRST:
            if (matchingLength < 1) { throw new NoSuchElementException("Constructor not found"); }
            return ((Constructors<T>) matching[0]).invoke(args);
        default:
            throw new UnsupportedOperationException("Unsupported selection mode " + selectionMode);
        }
    }
}
