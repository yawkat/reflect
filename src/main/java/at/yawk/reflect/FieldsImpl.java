/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * @author yawkat
 */
class FieldsImpl<T, R> extends MembersImpl<T, Field, FieldsImpl<T, R>> implements Fields<T, R> {
    FieldsImpl(Class<?> declaring) {
        super(Cache.getFields(declaring));
    }

    FieldsImpl() {}

    @Override
    protected FieldsImpl<T, R> createEmpty() {
        return new FieldsImpl<>();
    }

    @Override
    public FieldsImpl<T, R> mode(SelectionMode selectionMode) {
        if (selectionMode == SelectionMode.ALL) {
            throw new UnsupportedOperationException("Cannot use SelectionMode.ALL on fields");
        }
        return super.mode(selectionMode);
    }

    @Override
    public void eachField(ReflectiveConsumer<Field> consumer) throws UncheckedReflectiveOperationException {
        for (Member member : matching) {
            try {
                consumer.consume((Field) member);
            } catch (ReflectiveOperationException e) {
                throw new UncheckedReflectiveOperationException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <SubR extends R> SubR get() throws UncheckedReflectiveOperationException {
        return (SubR) get(handle);
    }

    @Override
    public void set(R value) throws UncheckedReflectiveOperationException {
        switch (selectionMode) {
        case ONLY:
            if (matchingLength > 1) {
                throw new IllegalStateException("Too many fields found: " + Arrays.toString(matching));
            }
            // same behaviour as first apart from this
        case FIRST:
            if (matchingLength < 1) { throw new NoSuchElementException("Field not found"); }
            doSet((Field) matching[0], handle, value);
            break;
        case ALL:
            for (Member member : matching) {
                doSet((Field) member, handle, value);
            }
            break;
        default:
            throw new UnsupportedOperationException("Unsupported selection mode " + selectionMode);
        }
    }

    @Override
    public void each(Consumer<R> consumer) {
        for (Member member : matching) {
            R value = doGet((Field) member, handle);
            consumer.accept(value);
        }
    }

    private void doSet(Field field, T on, R value) {
        int modifiers = field.getModifiers();
        boolean isFinal = Modifier.isFinal(modifiers);
        if (isFinal) {
            try {
                Cache.modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
            } catch (IllegalAccessException e) {
                throw new UncheckedReflectiveOperationException(e);
            }
        }
        try {
            field.set(on, value);
        } catch (IllegalAccessException e) {
            throw new UncheckedReflectiveOperationException(e);
        }
        if (isFinal) {
            try {
                Cache.modifiersField.setInt(field, modifiers);
            } catch (IllegalAccessException ignored) {}
        }
    }

    private R get(T instance) {
        switch (selectionMode) {
        case ONLY:
            if (matchingLength > 1) {
                throw new IllegalStateException("Too many fields found: " + this);
            }
            // same behaviour as first apart from this
        case FIRST:
            if (matchingLength < 1) { throw new NoSuchElementException("Field not found"); }
            return doGet((Field) matching[0], instance);
        default:
            throw new UnsupportedOperationException("Unsupported selection mode " + selectionMode);
        }
    }

    @SuppressWarnings("unchecked")
    private R doGet(Field field, T on) {
        try {
            return (R) field.get(on);
        } catch (IllegalAccessException e) {
            throw new UncheckedReflectiveOperationException(e);
        }
    }
}
