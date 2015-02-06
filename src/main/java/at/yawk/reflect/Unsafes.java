/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

import java.util.NoSuchElementException;
import sun.misc.Unsafe;

/**
 * @author yawkat
 */
public class Unsafes {
    private static final Unsafes instance;
    private final Unsafe unsafe;

    private Unsafes(Unsafe unsafe) {
        this.unsafe = unsafe;
    }

    static {
        Unsafe unsafe = null;
        try {
            // use theUnsafe if possible
            unsafe = Fields.of(Unsafe.class).name("theUnsafe").get();
        } catch (UncheckedReflectiveOperationException e) {
            try {
                // some platforms don't have theUnsafe, construct instead
                unsafe = Constructors.of(Unsafe.class).invoke();
            } catch (UncheckedReflectiveOperationException ignored) {}
        }
        instance = unsafe == null ? null : new Unsafes(unsafe);
    }

    public static boolean hasUnsafe() {
        return instance != null;
    }

    public static Unsafe getUnsafe() {
        return getInstance().unsafe;
    }

    public static Unsafes of(Unsafe unsafe) {
        return new Unsafes(unsafe);
    }

    public static Unsafes getInstance() {
        if (!hasUnsafe()) {
            throw new NoSuchElementException("Unsafe is not available");
        }
        return instance;
    }

    /**
     * Change the global security manager (<code>System.security</code>) while bypassing the System.setSecurityManager
     * permission check.
     *
     * Don't do this.
     */
    public void setSecurityManager(SecurityManager replacement) throws UncheckedReflectiveOperationException {
        try {
            long errOffset = unsafe.staticFieldOffset(System.class.getDeclaredField("err"));
            long consOffset = unsafe.staticFieldOffset(System.class.getDeclaredField("cons"));
            // System.security is somewhere between these two
            long securityOffset = (consOffset + errOffset) / 2;
            Object systemBase = unsafe.staticFieldBase(System.class.getDeclaredField("cons"));

            // synchronize like setSecurityManager
            synchronized (System.class) {
                unsafe.putObjectVolatile(systemBase, securityOffset, replacement);
            }
        } catch (ReflectiveOperationException e) {
            throw new UncheckedReflectiveOperationException(e);
        }
    }
}
