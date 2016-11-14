package ru.ayaz;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserIgnoredSetTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private User user;

    @Before
    public final void before() throws invalidNicknameException {
        user = new User();
        user.setNickname("Ayaz");
    }

    @Test
    public void shouldNotThrowNullPointerExceptionWhenInitialized() {
        user.isInIgnoredSet("someone");
    }

    @Test
    public void shouldAddUserToIgnoredSetWhenNormal() {
        assertTrue(user.ignoreUser("badGuy333"));
    }

    @Test
    public void shouldNotAddUserToIgnoredSetWhenContains() {
        user.ignoreUser("spammer423");
        assertFalse(user.ignoreUser("spammer423"));
    }

    @Test
    public void shouldNotAddUserToIgnoredSetWhenYourself() {
        assertFalse(user.ignoreUser("Ayaz"));
    }

}