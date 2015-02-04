/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
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
        for (int i = 0; i < matchingLength; i++) {
            try {
                consumer.consume((Constructor<T>) matching[i]);
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
            for (int i = 0; i < matchingLength; i++) {
                returnValue = newInstance((Constructor<T>) matching[i], args);
            }
            return returnValue;
        case ONLY:
            if (matchingLength > 1) {
                throw new IllegalStateException("Too many constructors found: " + this);
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
