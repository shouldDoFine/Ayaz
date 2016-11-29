package ru.ayaz;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.ayaz.ru.ayaz.exceptions.InvalidNicknameException;

import static org.junit.Assert.assertEquals;

public class UserNicknameTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldThrowWhenNullStringUsedAsNickname() throws Exception {
        exception.expect(InvalidNicknameException.class);
        new User(null);
    }

    @Test
    public void shouldThrowWhenEmptyStringAsNickname() throws Exception {
        exception.expect(InvalidNicknameException.class);
        new User("");
    }

    @Test
    public void shouldThrowWhenOnlySpacesAsNickname() throws Exception {
        exception.expect(InvalidNicknameException.class);
        new User("   ");
    }

    @Test
    public void shouldThrowWhenStringHasDigitFirstAsNickname() throws Exception {
        exception.expect(InvalidNicknameException.class);
        new User("4get");
    }

    @Test
    public void shouldAllowForNewNicknames() throws Exception {
        User user = new User("geeseWatcher123");
        assertEquals("geeseWatcher123", user.getNickname());
    }
}