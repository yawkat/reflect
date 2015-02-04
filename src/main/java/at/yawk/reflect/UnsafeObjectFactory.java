/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

import java.lang.reflect.Constructor;

/**
 * @author yawkat
 */
public class UnsafeObjectFactory {
    private static final Cloner DEFAULT_CLONER = UnsafeClonerBuilder.builder().defaults().build();

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
        return DEFAULT_CLONER.shallowClone(object);
    }

    public static <T> T deepClone(T object) {
        return DEFAULT_CLONER.deepClone(object);
    }
}
