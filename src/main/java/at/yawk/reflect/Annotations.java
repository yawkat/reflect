/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author yawkat
 */
public class Annotations {
    private Annotations() {}

    // nullable
    public static <A extends Annotation> A locateAnnotation(Class<A> type, Method method) {
        return doLocateAnnotation(type, method.getDeclaringClass(), method);
    }

    private static <A extends Annotation> A doLocateAnnotation(Class<A> type, Class<?> on, Method upperMethod) {
        if (on == null) { return null; }
        for (Method method : on.getDeclaredMethods()) {
            if (Cache.areEquivalent(method, upperMethod)) {
                A annotation = upperMethod.getAnnotation(type);
                if (annotation != null) { return annotation; }
            }
        }
        A annotation = doLocateAnnotation(type, on.getSuperclass(), upperMethod);
        if (annotation != null) { return annotation; }

        for (Class<?> itf : on.getInterfaces()) {
            annotation = doLocateAnnotation(type, itf, upperMethod);
            if (annotation != null) { return annotation; }
        }
        return null;
    }
}
