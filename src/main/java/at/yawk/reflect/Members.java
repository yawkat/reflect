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
