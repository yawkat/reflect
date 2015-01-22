/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

/**
 * @author yawkat
 */
public enum SelectionMode {
    /**
     * Only use the first member and fail when no members were found.
     */
    FIRST,
    /**
     * Only use the first member and fail when more or less than one member were found.
     */
    ONLY,
    /**
     * Use all members and return the value of the last one, or null if no member was found.
     */
    ALL,
}
