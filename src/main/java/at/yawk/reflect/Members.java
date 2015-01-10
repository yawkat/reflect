package at.yawk.reflect;

/**
 * @author yawkat
 */
public interface Members<T, R> {
    Members<T, R> name(String name);

    Members<T, R> modifier(int modifier);

    Members<T, R> withoutModifier(int modifier);

    Members<T, R> mode(SelectionMode selectionMode);

    Members<T, R> first();

    Members<T, R> only();

    Members<T, R> finish();

    Members<T, R> statics();

    Members<T, R> on(T on);
}
