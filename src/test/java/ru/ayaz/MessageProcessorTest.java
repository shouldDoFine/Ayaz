package ru.ayaz;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MessageProcessorTest {
    private MessageProcessor processor;

    @Before
    public void before() {
        processor = new MessageProcessor();
    }

    @Test
    public void falseWhenSharpNotFirst(){
        assertEquals(false, processor.isCommand("d#quit"));
    }

    @Test
    public void trueWhenSharpFirstAfterSpaces(){
        assertEquals(true, processor.isCommand("    #quit"));
    }

    @Test
    public void shouldGetCommandWhenNormalStartSpaces(){
        assertEquals("#quit", processor.getCommand("    #quit"));
    }

    @Test
    public void shouldGetCommandWhenNormalTwoWords(){
        assertEquals("#ignore", processor.getCommand("#ignore spammer"));
    }


    @Test
    public void shouldGetArgumentWhenOneArgument(){
        String result = processor.getFirstArgument("  #ignore spammer");
        assertEquals("spammer", result);
    }

    @Test
    public void shouldGetFirstArgumentWhenTwoArguments(){
        String result = processor.getFirstArgument("  #ignore spammer badGuy123");
        assertEquals("spammer", result);
    }



}