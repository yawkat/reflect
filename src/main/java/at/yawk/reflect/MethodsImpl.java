package at.yawk.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
class MethodsImpl<T, R> implements Methods<T, R> {
    /*
     * Implementation of the Methods interfaces. Lots of lazy computations.
     */

    /**
     * Array of methods matching the restrictions given to this builder. Actual used length is
     * #matchingLength, elements after that are to be ignored.
     */
    private Method[] matching;
    /**
     * Whether #matching should be considered mutable or if it should be copied before changing.
     */
    private boolean matchingModifiable;
    /**
     * @see #matching
     */
    private int matchingLength;
    /**
     * Selection mode when meeting multiple matching methods.
     */
    private SelectionMode selectionMode = SelectionMode.ONLY;
    /**
     * Whether this Methods object should be considered immutable.
     */
    private boolean immutable = false;
    /**
     * If this object has a handle (or null for statics).
     */
    private boolean hasHandle = false;
    /**
     * Object handle or null if this is currently a StaticMethods object.
     */
    private T handle = null;

    MethodsImpl(Class<?> declaring) {
        this.matching = Cache.getMethods(declaring);
        this.matchingModifiable = false;
        this.matchingLength = this.matching.length;
    }

    /**
     * Create a mutable copy of the given MethodsImpl.
     */
    private MethodsImpl(MethodsImpl<T, R> original) {
        this.hasHandle = original.hasHandle;
        this.handle = original.handle;
        this.matching = original.matching;
        this.matchingModifiable = false;
        this.matchingLength = original.matchingLength;
    }

    @Override
    public MethodsImpl<T, R> name(String name) {
        return match(m -> m.getName().equals(name));
    }

    @Override
    public MethodsImpl<T, R> modifier(int modifier) {
        return match(m -> (m.getModifiers() & modifier) == modifier);
    }

    @Override
    public MethodsImpl<T, R> withoutModifier(int modifier) {
        return match(m -> (m.getModifiers() & modifier) == 0);
    }

    @Override
    public MethodsImpl<T, R> match(Predicate<Method> predicate) {
        Objects.requireNonNull(predicate, "predicate");

        // 'this' or a modifiable copy
        MethodsImpl<T, R> modifiable = modifiable();
        // array we're moving accepted methods to. Can be #matching or a copy of it. Initialized when needed.
        Method[] targetArray = null;
        int back = 0;
        for (int i = 0; i < modifiable.matchingLength; i++) {
            Method method = modifiable.matching[i];
            if (predicate.test(method)) {
                // if we have to a) move array elements back or b) copy the array
                if (back != 0 || !modifiable.matchingModifiable) {
                    // create array lazily
                    if (targetArray == null) {
                        // reuse either our array or a copy
                        targetArray = modifiable.matchingModifiable ?
                                modifiable.matching :
                                // clip to matchingLength
                                Arrays.copyOf(modifiable.matching, modifiable.matchingLength);
                    }
                    // move back
                    targetArray[i - back] = method;
                }
            } else {
                // create array lazily
                if (targetArray == null) {
                    // reuse either our array or a copy
                    targetArray = modifiable.matchingModifiable ?
                            modifiable.matching :
                            // don't need matchingLength, maximum one less
                            Arrays.copyOf(modifiable.matching, modifiable.matchingLength - 1);
                }
                // move following entries one more back
                back++;
            }
        }
        // change matchingLength according to removed elements
        modifiable.matchingLength -= back;
        // if we created a targetArray, use that
        if (targetArray != null) {
            modifiable.matching = targetArray;
            modifiable.matchingModifiable = true;
        }
        return modifiable;
    }

    /**
     * Return this object or a modifiable copy if this object is immutable.
     */
    private MethodsImpl<T, R> modifiable() {
        return immutable ? new MethodsImpl<>(this) : this;
    }

    @Override
    public MethodsImpl<T, R> mode(SelectionMode selectionMode) {
        MethodsImpl<T, R> modifiable = modifiable();
        modifiable.selectionMode = Objects.requireNonNull(selectionMode, "selectionMode");
        return modifiable;
    }

    @Override
    public MethodsImpl<T, R> first() {
        return mode(SelectionMode.FIRST);
    }

    @Override
    public MethodsImpl<T, R> all() {
        return mode(SelectionMode.ALL);
    }

    @Override
    public MethodsImpl<T, R> only() {
        return mode(SelectionMode.ONLY);
    }

    @Override
    public MethodsImpl<T, R> finish() {
        this.immutable = true;
        this.matchingModifiable = false;
        return this;
    }

    @Override
    public Methods<T, R> statics() {
        return on0(null);
    }

    @Override
    public MethodsImpl<T, R> on(T on) {
        return on0(Objects.requireNonNull(on));
    }

    @Override
    public R invoke(Object... args) {
        return invoke(handle, args);
    }

    private R invoke(T instance, Object... args) {
        switch (selectionMode) {
        case ALL:
            R returnValue = null;
            for (Method method : matching) {
                returnValue = doInvoke(method, instance, args);
            }
            return returnValue;
        case ONLY:
            if (matchingLength > 1) {
                throw new IllegalStateException("Too many methods found: " + Arrays.toString(matching));
            }
            // same behaviour as first apart from this
        case FIRST:
            if (matchingLength < 1) { throw new NoSuchElementException("Method not found"); }
            return doInvoke(matching[0], instance, args);
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

    private MethodsImpl<T, R> on0(T on) {
        if (hasHandle) {
            throw new IllegalStateException("Handle already set!");
        }
        MethodsImpl<T, R> modifiable = modifiable();
        modifiable.hasHandle = true;
        modifiable.handle = on;
        return modifiable;
    }
}
