package ru.ayaz;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Created by ayaz on 11/5/16.
 */
public class UserTest {

    private User user;

    @Before
    public final void before(){ user = new User(); }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void passingNull_setName() throws Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickName(null);
    }

    @Test
    public void passingEmpty_setName() throws  Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickName("");
    }

    @Test
    public void passingSpaces_setName() throws  Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickName("   ");
    }

    @Test
    public void passingFirstDigit_setName() throws  Exception {
        exception.expect(IllegalArgumentException.class);
        user.setNickName("4get");
    }

    @Test
    public void passingNormalName_setName() {
        user.setNickName("geeseWatcher123");
        Assert.assertEquals("geeseWatcher123", user.getNickName());
    }

}