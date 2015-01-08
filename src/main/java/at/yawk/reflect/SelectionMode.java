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
