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
    public void shouldThrowIllegalArgExceptionWhenNull() throws Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickname(null);
    }

    @Test
    public void shouldThrowIllegalArgExceptionWhenEmpty() throws Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickname("");
    }

    @Test
    public void shouldThrowIllegalArgExceptionWhenSpaces() throws Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickname("   ");
    }

    @Test
    public void shouldThrowIllegalArgExceptionWhenFirstDigit() throws Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickname("4get");
    }

    @Test
    public void shouldNotThrowIllegalArgExceptionWhenNormalString() {
        user.setNickname("geeseWatcher123");
        assertEquals("geeseWatcher123", user.getNickname());
    }


}