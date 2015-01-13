package at.yawk.reflect;

/**
 * Consumer that may throw a ReflectiveOperationException.
 *
 * @author yawkat
 */
public interface ReflectiveConsumer<T> {
    void consume(T t) throws ReflectiveOperationException;
}
