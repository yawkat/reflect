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
    private static final Unsafe UNSAFE;

    private Unsafes() {}

    static {
        Unsafe instance = null;
        try {
            // use theUnsafe if possible
            instance = Fields.of(Unsafe.class).name("theUnsafe").get();
        } catch (UncheckedReflectiveOperationException e) {
            try {
                // some platforms don't have theUnsafe, construct instead
                instance = Constructors.of(Unsafe.class).invoke();
            } catch (UncheckedReflectiveOperationException ignored) {}
        }
        UNSAFE = instance;
    }

    public static boolean hasUnsafe() {
        return UNSAFE != null;
    }

    public static Unsafe getUnsafe() {
        if (!hasUnsafe()) {
            throw new NoSuchElementException("Unsafe is not available");
        }
        return UNSAFE;
    }
}
