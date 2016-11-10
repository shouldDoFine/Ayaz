package ru.ayaz;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class UserIgnoredSetTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private User user;

    @Before
    public final void before() {
        user = new User();
        user.setNickname("Ayaz");
    }

    @Test
    public void notNullPointerExceptionWhenInitialized() {
        user.hasInIgnoredSet("someone");
    }

    @Test
    public void addUserToIgnoredSetWhenNormal() {
        assertEquals(true, user.ignoreUser("badGuy333"));
    }

    @Test
    public void dontAddUserToIgnoredSetWhenContains() {
        user.ignoreUser("spammer423");
        assertEquals(false, user.ignoreUser("spammer423"));
    }

    @Test
    public void dontAddUserToIgnoredSetWhenYourself() {
        assertEquals(false, user.ignoreUser("Ayaz"));
    }

}