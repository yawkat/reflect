package at.yawk.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
abstract class MembersImpl<T, R, M extends Member, S extends MembersImpl<T, R, M, S>>
        implements Members<T, R> {
    /*
     * Implementation of the Methods interfaces. Lots of lazy computations.
     */

    /**
     * Array of methods matching the restrictions given to this builder. Actual used length is
     * #matchingLength, elements after that are to be ignored.
     */
    Member[] matching;
    /**
     * Whether #matching should be considered mutable or if it should be copied before changing.
     */
    boolean matchingModifiable;
    /**
     * @see #matching
     */
    int matchingLength;
    /**
     * Selection mode when meeting multiple matching methods.
     */
    SelectionMode selectionMode = SelectionMode.ONLY;
    /**
     * Whether this Methods object should be considered immutable.
     */
    boolean immutable = false;
    /**
     * If this object has a handle (or null for statics).
     */
    boolean hasHandle = false;
    /**
     * Object handle or null if this is currently a StaticMethods object.
     */
    T handle = null;

    MembersImpl(Member[] members) {
        this.matching = members;
        this.matchingModifiable = false;
        this.matchingLength = this.matching.length;
    }

    MembersImpl() {}

    protected abstract S createEmpty();

    @Override
    public S name(String name) {
        return match(m -> m.getName().equals(name));
    }

    @Override
    public S modifier(int modifier) {
        return match(m -> (m.getModifiers() & modifier) == modifier);
    }

    @Override
    public S withoutModifier(int modifier) {
        return match(m -> (m.getModifiers() & modifier) == 0);
    }

    @SuppressWarnings("unchecked")
    public S match(Predicate<M> predicate) {
        Objects.requireNonNull(predicate, "predicate");

        // 'this' or a modifiable copy
        S modifiable = modifiable();
        // array we're moving accepted methods to. Can be #matching or a copy of it. Initialized when needed.
        Member[] targetArray = null;
        int back = 0;
        for (int i = 0; i < modifiable.matchingLength; i++) {
            Member member = modifiable.matching[i];
            if (predicate.test((M) member)) {
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
                    targetArray[i - back] = member;
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
    @SuppressWarnings("unchecked")
    private S modifiable() {
        if (immutable) {
            S n = createEmpty();
            n.hasHandle = this.hasHandle;
            n.handle = this.handle;
            n.matching = this.matching;
            n.matchingModifiable = false;
            n.matchingLength = this.matchingLength;
            return n;
        } else {
            return (S) this;
        }
    }

    @Override
    public S mode(SelectionMode selectionMode) {
        S modifiable = modifiable();
        modifiable.selectionMode = Objects.requireNonNull(selectionMode, "selectionMode");
        return modifiable;
    }

    @Override
    public S first() {
        return mode(SelectionMode.FIRST);
    }

    @Override
    public S only() {
        return mode(SelectionMode.ONLY);
    }

    @SuppressWarnings("unchecked")
    @Override
    public S finish() {
        this.immutable = true;
        this.matchingModifiable = false;
        return (S) this;
    }

    @Override
    public S statics() {
        return on0(null);
    }

    @Override
    public S on(T on) {
        return on0(Objects.requireNonNull(on));
    }

    private S on0(T on) {
        if (hasHandle) {
            throw new IllegalStateException("Handle already set!");
        }
        S modifiable = modifiable();
        modifiable.hasHandle = true;
        modifiable.handle = on;
        return modifiable;
    }
}
