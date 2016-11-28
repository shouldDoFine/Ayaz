package ru.ayaz;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static ru.ayaz.MessageParser.*;

public class MessageParserTest {


    @Test
    public void shouldNotBeTreatedAsCommandWhenCharacterComesFirst() {
        assertEquals(false, isCommand("d#quit"));
    }

    @Test
    public void shouldBeTreatedAsCommandWhenSharpComesRightAfterSpaces() {
        assertEquals(true, isCommand("    #quit"));
    }

    @Test
    public void shouldGetCommandWhenSharpComesFirst() {
        assertEquals("#quit", getCommand("    #quit"));
    }

    @Test
    public void shouldGetCommandWhenStringHasTwoWords() {
        assertEquals("#ignore", getCommand("#ignore spammer"));
    }


    @Test
    public void shouldGetArgumentWhenStringHasOneArgument() {
        String result = getFirstArgument("  #ignore spammer");
        assertEquals("spammer", result);
    }

    @Test
    public void shouldGetFirstArgumentWhenStringHasTwoArguments() {
        String result = getFirstArgument("  #ignore spammer badGuy123");
        assertEquals("spammer", result);
    }
}