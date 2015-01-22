package at.yawk.reflect;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yawkat
 */
public class FieldsTest {
    @Test
    public void testInstanceFieldsBase() {
        ClassWithOneMember obj = new ClassWithOneMember();
        Fields.of(obj).set("test");
        assertEquals("test", obj.string);
    }

    @Test
    public void testInstanceFieldsByClass() {
        ClassWithOneMember obj = new ClassWithOneMember();
        Fields.ofType(ClassWithOneMember.class).on(obj).set("test");
        assertEquals("test", obj.string);
    }
}

class ClassWithOneMember {
    String string = null;
}
