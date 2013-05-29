package statik.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PasswordValidatorTest {

    private PasswordValidator p;

    @Before
    public void setUp() {
        p = new PasswordValidator();
    }

    @Test
    public void passingPasswords() {
        assertTrue("Should pass", p.validPasswords("this is a long password", "this is a long password"));
    }

    @Test
    public void failOnNullPassword() {
        assertFalse("Should be invalid", p.validPasswords(null, null));
    }

    @Test
    public void failOnBlankPassword() {
        assertFalse("Should be invalid", p.validPasswords("", ""));
    }

    @Test
    public void failOnPasswordsShorterThan8Chars(){
        assertFalse("Should be too short", p.validPasswords("1234567", "1234567"));
    }


    @Test
    public void failOnNonMatchingPassword() {
        assertFalse("Shouldn't pass; non matching", p.validPasswords("password", "something else"));
    }


    @Test
    public void failOn3orMoreRepeatedDigits() {
        assertFalse("Should fail for repeated digits", p.validPasswords("111abcd","111abcd"));
    }

    @Test
    public void failOn3orMoreRepeatedLetters() {
        assertFalse("Should fail for repeated letters", p.validPasswords("AAAabcd","AAAabcd"));
    }


}
