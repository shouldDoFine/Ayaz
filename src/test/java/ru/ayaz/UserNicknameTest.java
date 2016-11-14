package ru.ayaz;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class UserNicknameTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private User user;

    @Before
    public final void before() {
        user = new User();
    }

    @Test
    public void shouldThrowWhenNullUsedAsNickname() throws Exception {
        exception.expect(invalidNicknameException.class);
        user.setNickname(null);
    }

    @Test
    public void shouldThrowWhenEmptyAsNickname() throws Exception {
        exception.expect(invalidNicknameException.class);
        user.setNickname("");
    }

    @Test
    public void shouldThrowWhenSpacesAsNickname() throws Exception {
        exception.expect(invalidNicknameException.class);
        user.setNickname("   ");
    }

    @Test
    public void shouldThrowWhenStringHasDigitFirstAsNickname() throws Exception {
        exception.expect(invalidNicknameException.class);
        user.setNickname("4get");
    }

    @Test
    public void shouldNotThrowWhenStringIsNotAsOtherTestsNicknames() throws Exception {
        user.setNickname("geeseWatcher123");
        assertEquals("geeseWatcher123", user.getNickname());
    }


}