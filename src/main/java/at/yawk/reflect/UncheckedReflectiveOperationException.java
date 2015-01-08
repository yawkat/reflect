package at.yawk.reflect;

/**
 * @author yawkat
 */
public class UncheckedReflectiveOperationException extends RuntimeException {
    public UncheckedReflectiveOperationException(ReflectiveOperationException cause) {
        super(cause);
    }
}
