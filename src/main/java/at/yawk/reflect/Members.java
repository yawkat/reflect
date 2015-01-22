/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.reflect;

/**
 * @author yawkat
 */
public interface Members<T> {
    Members<T> name(String name);

    Members<T> modifier(int modifier);

    Members<T> withoutModifier(int modifier);

    Members<T> mode(SelectionMode selectionMode);

    Members<T> first();

    Members<T> only();

    Members<T> finish();

    Members<T> statics();

    Members<T> on(T on);
}
