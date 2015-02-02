/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author yawkat
 */
public class UnsafeObjectFactory {
    private UnsafeObjectFactory() {}

    public static <T> T createInstance(Class<T> type) {
        try {
            Constructor<T> constructor = type.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            // fall back on unsafe
            return allocateInstance(type);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T allocateInstance(Class<T> type) {
        try {
            return (T) Unsafes.getUnsafe().allocateInstance(type);
        } catch (InstantiationException e) {
            throw new UncheckedReflectiveOperationException(e);
        }
    }

    public static <T> void copyFields(T from, T to) {
        Fields.of(from).eachField(f -> f.set(to, f.get(from)));
    }

    public static <T> T shallowClone(T object) {
        return doClone(object, true);
    }

    @SuppressWarnings({ "unchecked", "SuspiciousSystemArraycopy" })
    private static <T> T doClone(T object, boolean copyFields) {
        if (object == null) { return null; }

        Class<T> type = (Class<T>) object.getClass();

        T copy;
        if (type.isArray()) {
            int length = Array.getLength(object);
            copy = (T) Array.newInstance(type.getComponentType(), length);
            if (copyFields) {
                System.arraycopy(object, 0, copy, 0, length);
            }
        } else {
            copy = allocateInstance(type);
            if (copyFields) {
                copyFields(object, copy);
            }
        }

        return copy;
    }

    @SuppressWarnings("unchecked")
    public static <T> T deepClone(T object) {
        Map<Object, Object> clones = new IdentityHashMap<>();

        cloneDeepWithoutFieldCopy(object, clones);

        clones.forEach((fr, to) -> {
            if (fr.getClass().isArray()) {
                int len = Array.getLength(fr);
                for (int i = 0; i < len; i++) {
                    Array.set(to, i, clones.get(Array.get(fr, i)));
                }
            } else {
                Fields.of(fr).eachField(f -> f.set(to, clones.get(f.get(fr))));
            }
        });

        return (T) clones.get(object);
    }

    private static void cloneDeepWithoutFieldCopy(Object object, Map<Object, Object> to) {
        if (object == null) { return; }
        if (to.containsKey(object)) { return; }

        Object copy = doClone(object, false);
        to.put(object, copy);

        if (object.getClass().isArray()) {
            int len = Array.getLength(object);
            for (int i = 0; i < len; i++) {
                cloneDeepWithoutFieldCopy(Array.get(object, i), to);
            }
        } else {
            Fields.of(object).each(o -> cloneDeepWithoutFieldCopy(o, to));
        }
    }
}
