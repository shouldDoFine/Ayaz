package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageProcessorTest {

    private MessageParser processor;

    @Before
    public void before() {
        processor = new MessageParser();
    }


    @Test
    public void shouldNotBeTreatedAsCommandWhenCharacterComesFirst() {
        assertEquals(false, processor.isCommand("d#quit"));
    }

    @Test
    public void shouldBeTreatedAsCommandWhenSharpComesRightAfterSpaces() {
        assertEquals(true, processor.isCommand("    #quit"));
    }

    @Test
    public void shouldGetCommandWhenSharpComesFirst() {
        assertEquals("#quit", processor.getCommand("    #quit"));
    }

    @Test
    public void shouldGetCommandWhenStringHasTwoWords() {
        assertEquals("#ignore", processor.getCommand("#ignore spammer"));
    }


    @Test
    public void shouldGetArgumentWhenStringHasOneArgument() {
        String result = processor.getFirstArgument("  #ignore spammer");
        assertEquals("spammer", result);
    }

    @Test
    public void shouldGetFirstArgumentWhenStringHasTwoArguments() {
        String result = processor.getFirstArgument("  #ignore spammer badGuy123");
        assertEquals("spammer", result);
    }
}