package org.storage.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.storage.common.Utils.bytesToHex;

public class UtilsTest {

    @Test
    public void testBytesToHex() {
        byte[] bytes = new byte[]{0x00, 0x0F, 0x10, (byte) 0xFF};
        String expected = "000f10ff";
        String actual = bytesToHex(bytes);
        assertEquals(expected, actual);
    }

    @Test
    void testBytesToHexEmptyArray() {
        byte[] bytes = new byte[]{};
        String expected = "";
        assertEquals(expected, bytesToHex(bytes));
    }

    @Test
    void testBytesToHexSingleByte() {
        byte[] bytes = new byte[]{(byte) 0xAB};
        String expected = "ab";
        assertEquals(expected, bytesToHex(bytes));
    }
}
