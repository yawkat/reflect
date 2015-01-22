/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

/**
 * Consumer that may throw a ReflectiveOperationException.
 *
 * @author yawkat
 */
public interface ReflectiveConsumer<T> {
    void consume(T t) throws ReflectiveOperationException;
}
