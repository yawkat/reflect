/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

/**
 * @author yawkat
 */
public interface Cloner {
    <T> T shallowClone(T obj);

    <T> T deepClone(T obj);
}
