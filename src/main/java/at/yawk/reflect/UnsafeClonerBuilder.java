/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author yawkat
 */
public class UnsafeClonerBuilder {
    private final Set<Object> protectedObjects = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<Class<?>> protectedClasses = new HashSet<>();

    private UnsafeClonerBuilder() {}

    public static UnsafeClonerBuilder builder() {
        return new UnsafeClonerBuilder();
    }

    public UnsafeClonerBuilder defaults() {
        protect(String.class);
        protect(System.class);
        protect(Class.class);
        protect(Field.class);
        protect(Method.class);
        protect(Constructor.class);
        protect(Boolean.class);
        protect(Byte.class);
        protect(Short.class);
        protect(Integer.class);
        protect(Long.class);
        protect(Float.class);
        protect(Double.class);
        return this;
    }

    public UnsafeClonerBuilder protect(Object o) {
        protectedObjects.add(o);
        return this;
    }

    public UnsafeClonerBuilder protect(Class<?> type) {
        protectedClasses.add(type);
        return this;
    }

    public Cloner build() {
        Set<Object> protectedObjects0 = Collections.newSetFromMap(new IdentityHashMap<>());
        protectedObjects0.addAll(protectedObjects);
        return new UnsafeCloner(
                protectedObjects0,
                new HashSet<>(protectedClasses)
        );
    }

    private static class UnsafeCloner implements Cloner {
        private final Set<Object> protectedObjects;
        private final Set<Class<?>> protectedClasses;

        public UnsafeCloner(Set<Object> protectedObjects, Set<Class<?>> protectedClasses) {
            this.protectedObjects = protectedObjects;
            this.protectedClasses = protectedClasses;
        }

        private boolean isProtected(Object o) {
            return o == null ||
                   protectedObjects.contains(o) ||
                   protectedClasses.contains(o.getClass());
        }

        @Override
        public <T> T shallowClone(T object) {
            if (isProtected(object)) { return object; }
            return doClone(object, true);
        }

        @SuppressWarnings({ "unchecked", "SuspiciousSystemArraycopy" })
        private <T> T doClone(T object, boolean copyFields) {
            Class<T> type = (Class<T>) object.getClass();

            T copy;
            if (type.isArray()) {
                int length = Array.getLength(object);
                copy = (T) Array.newInstance(type.getComponentType(), length);
                if (copyFields) {
                    System.arraycopy(object, 0, copy, 0, length);
                }
            } else {
                copy = UnsafeObjectFactory.allocateInstance(type);
                if (copyFields) {
                    UnsafeObjectFactory.copyFields(object, copy);
                }
            }

            return copy;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T deepClone(T object) {
            Map<Object, Object> clones = new IdentityHashMap<>();

            cloneDeepWithoutFieldCopy(object, clones);

            clones.forEach((fr, to) -> {
                if (fr.getClass().isArray()) {
                    int len = Array.getLength(fr);
                    for (int i = 0; i < len; i++) {
                        Object previous = Array.get(fr, i);
                        Array.set(to, i, clones.getOrDefault(previous, previous));
                    }
                } else {
                    Fields.of(fr)
                            .withoutModifier(Modifier.STATIC)
                            .eachField(f -> {
                                Object previous = f.get(fr);
                                f.set(to, clones.getOrDefault(previous, previous));
                            });
                }
            });

            return (T) clones.get(object);
        }

        private void cloneDeepWithoutFieldCopy(Object object, Map<Object, Object> to) {
            if (isProtected(object)) { return; }
            if (to.containsKey(object)) { return; }

            Object copy = doClone(object, false);
            to.put(object, copy);

            if (object.getClass().isArray()) {
                int len = Array.getLength(object);
                for (int i = 0; i < len; i++) {
                    cloneDeepWithoutFieldCopy(Array.get(object, i), to);
                }
            } else {
                Fields.of(object)
                        .withoutModifier(Modifier.STATIC)
                        .each(o -> cloneDeepWithoutFieldCopy(o, to));
            }
        }
    }
}
