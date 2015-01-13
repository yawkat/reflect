package at.yawk.reflect;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author yawkat
 */
class Cache {
    private static final Map<Class<?>, Reference<Method[]>> methodCache = new WeakHashMap<>();

    static synchronized Method[] getMethods(Class<?> clazz) {
        Reference<Method[]> cached = methodCache.get(clazz);
        if (cached != null) {
            Method[] methods = cached.get();
            if (methods != null) {
                return methods.clone();
            }
        }
        List<Method> l = new ArrayList<>();
        collectDeclaredMethods(clazz, l);
        // We should probably use a weak ref on the methods so they don't keep their declaring
        // classes from being collected, but the overhead of an array of weak refs would be too
        // high. We also can't weak ref the array itself as it would be collected immediately.
        // Instead, we will settle for a soft ref for now.
        Method[] methods = l.toArray(new Method[l.size()]);
        methodCache.put(clazz, new SoftReference<>(methods));
        return methods.clone();
    }

    private static synchronized void collectDeclaredMethods(Class<?> clazz, List<Method> into) {
        if (clazz == null) { return; } // recursion end

        int foundBefore = into.size();
        outer:
        for (Method method : clazz.getDeclaredMethods()) {
            for (int i = 0; i < foundBefore; i++) {
                Method other = into.get(i);
                if (!Modifier.isPrivate(other.getModifiers()) &&
                    other.getName().equals(method.getName()) &&
                    other.getReturnType().isAssignableFrom(method.getReturnType()) &&
                    Arrays.equals(other.getParameterTypes(), method.getParameterTypes())) {
                    continue outer;
                }
            }
            if (!method.isAccessible()) {
                try {
                    method.setAccessible(true);
                } catch (SecurityException e) {
                    // silently fail and don't add to result list
                    continue;
                }
            }
            into.add(method);
        }
        collectDeclaredMethods(clazz.getSuperclass(), into);
        for (Class<?> iface : clazz.getInterfaces()) {
            collectDeclaredMethods(iface, into);
        }
    }

    //////////////

    static final Field modifiersField;

    static {
        Field f;
        try {
            f = Field.class.getDeclaredField("modifiers");
            f.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            f = null;
        }
        modifiersField = f;
    }

    private static final Map<Class<?>, Reference<Field[]>> fieldCache = new WeakHashMap<>();

    static synchronized Field[] getFields(Class<?> clazz) {
        Reference<Field[]> cached = fieldCache.get(clazz);
        if (cached != null) {
            Field[] fields = cached.get();
            if (fields != null) {
                return fields.clone();
            }
        }
        List<Field> l = new ArrayList<>();
        collectDeclaredFields(clazz, l);
        // We should probably use a weak ref on the methods so they don't keep their declaring
        // classes from being collected, but the overhead of an array of weak refs would be too
        // high. We also can't weak ref the array itself as it would be collected immediately.
        // Instead, we will settle for a soft ref for now.
        Field[] fields = l.toArray(new Field[l.size()]);
        fieldCache.put(clazz, new SoftReference<>(fields));
        return fields.clone();
    }

    private static synchronized void collectDeclaredFields(Class<?> clazz, List<Field> into) {
        if (clazz == null) { return; } // recursion end

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAccessible()) {
                try {
                    field.setAccessible(true);
                } catch (SecurityException e) {
                    // silently fail and don't add to result list
                    continue;
                }
            }
            into.add(field);
        }
        collectDeclaredFields(clazz.getSuperclass(), into);
        for (Class<?> iface : clazz.getInterfaces()) { // static fields
            collectDeclaredFields(iface, into);
        }
    }

    //////////////

    private static final Map<Class<?>, Reference<Constructor<?>[]>> constructorCache = new WeakHashMap<>();

    static synchronized Constructor<?>[] getConstructors(Class<?> clazz) {
        Reference<Constructor<?>[]> cached = constructorCache.get(clazz);
        if (cached != null) {
            Constructor<?>[] methods = cached.get();
            if (methods != null) {
                return methods.clone();
            }
        }
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (!constructor.isAccessible()) {
                try {
                    constructor.setAccessible(true);
                } catch (SecurityException ignored) {}
            }
        }
        // We should probably use a weak ref on the methods so they don't keep their declaring
        // classes from being collected, but the overhead of an array of weak refs would be too
        // high. We also can't weak ref the array itself as it would be collected immediately.
        // Instead, we will settle for a soft ref for now.
        constructorCache.put(clazz, new SoftReference<>(constructors));
        return constructors.clone();
    }
}
