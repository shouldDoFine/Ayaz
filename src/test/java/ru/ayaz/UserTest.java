package ru.ayaz;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class UserTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private User user;

    @Before
    public final void before() {
        user = new User();
    }

    @Test
    public void whenNullThenIllegalArgException() throws Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickName(null);
    }

    @Test
    public void whenEmptyThenIllegalArgException() throws Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickName("");
    }

    @Test
    public void whenSpacesThenIllegalArgException() throws Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickName("   ");
    }

    @Test
    public void whenFirstDigitThenIllegalArgException() throws Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickName("4get");
    }

    @Test
    public void whenNormalNameThenSetName() {
        user.setNickName("geeseWatcher123");
        assertEquals("geeseWatcher123", user.getNickName());
    }

}