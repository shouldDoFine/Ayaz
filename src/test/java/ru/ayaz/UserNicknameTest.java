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
    public void shouldThrowWhenNullStringUsedAsNickname() throws Exception {
        exception.expect(InvalidNicknameException.class);
        user.setNickname(null);
    }

    @Test
    public void shouldThrowWhenEmptyStringAsNickname() throws Exception {
        exception.expect(InvalidNicknameException.class);
        user.setNickname("");
    }

    @Test
    public void shouldThrowWhenOnlySpacesAsNickname() throws Exception {
        exception.expect(InvalidNicknameException.class);
        user.setNickname("   ");
    }

    @Test
    public void shouldThrowWhenStringHasDigitFirstAsNickname() throws Exception {
        exception.expect(InvalidNicknameException.class);
        user.setNickname("4get");
    }

    @Test
    public void shouldAllowForNewNicknames() throws Exception {
        user.setNickname("geeseWatcher123");
        assertEquals("geeseWatcher123", user.getNickname());
    }


}