package statik.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UsernameValidatorTest {

    private UsernameValidator v;

    @Before
    public void setUp() {
        v = new UsernameValidator();
    }

    @Test
    public void valid() {
        assertTrue("Should be valid", v.validUsername("test@example.com"));
    }

    @Test
    public void invalidWhenBlank() {
        assertFalse("Should be invalid", v.validUsername(""));
    }

    @Test
    public void invalidWhenNull() {
        assertFalse("Should be invalid", v.validUsername(null));
    }


    @Test
    public void invalidWhenNotAnEmailAddress() {
        assertFalse("Should be invalid", v.validUsername("username"));
    }


    @Test
    public void invalidWhenHasNoDomain() {
        assertFalse("Should be invalid", v.validUsername("test@"));
    }

    @Test
    public void invalidWhenHasNoPrefix() {
        assertFalse("Should be invalid", v.validUsername("@example.com"));
    }

}
