package com.urlshortener.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Base62Encoder utility class
 */
@DisplayName("Base62Encoder Tests")
class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @Test
    @DisplayName("Should encode zero correctly")
    void testEncodeZero() {
        String result = base62Encoder.encode(0);
        assertEquals("0", result);
    }

    @Test
    @DisplayName("Should encode single digit numbers correctly")
    void testEncodeSingleDigit() {
        assertEquals("1", base62Encoder.encode(1));
        assertEquals("9", base62Encoder.encode(9));
        assertEquals("a", base62Encoder.encode(10));
        assertEquals("z", base62Encoder.encode(35));
        assertEquals("A", base62Encoder.encode(36));
        assertEquals("Z", base62Encoder.encode(61));
    }

    @Test
    @DisplayName("Should encode larger numbers correctly")
    void testEncodeLargerNumbers() {
        assertEquals("10", base62Encoder.encode(62));
        assertEquals("100", base62Encoder.encode(3844)); // 62^2
        assertEquals("1000", base62Encoder.encode(238328)); // 62^3
    }

    @Test
    @DisplayName("Should decode correctly")
    void testDecode() {
        assertEquals(0, base62Encoder.decode("0"));
        assertEquals(1, base62Encoder.decode("1"));
        assertEquals(10, base62Encoder.decode("a"));
        assertEquals(35, base62Encoder.decode("z"));
        assertEquals(36, base62Encoder.decode("A"));
        assertEquals(61, base62Encoder.decode("Z"));
        assertEquals(62, base62Encoder.decode("10"));
        assertEquals(3844, base62Encoder.decode("100"));
    }

    @Test
    @DisplayName("Should handle encode-decode round trip correctly")
    void testEncodeDecodeRoundTrip() {
        long[] testNumbers = {0, 1, 10, 62, 100, 1000, 12345, 999999, Long.MAX_VALUE / 1000};
        
        for (long number : testNumbers) {
            String encoded = base62Encoder.encode(number);
            long decoded = base62Encoder.decode(encoded);
            assertEquals(number, decoded, "Round trip failed for number: " + number);
        }
    }

    @Test
    @DisplayName("Should encode with minimum length correctly")
    void testEncodeWithMinLength() {
        assertEquals("000001", base62Encoder.encodeWithMinLength(1, 6));
        assertEquals("00000a", base62Encoder.encodeWithMinLength(10, 6));
        assertEquals("0000Z", base62Encoder.encodeWithMinLength(61, 5));
        assertEquals("10", base62Encoder.encodeWithMinLength(62, 2)); // Already meets min length
        assertEquals("100", base62Encoder.encodeWithMinLength(3844, 2)); // Exceeds min length
    }

    @Test
    @DisplayName("Should validate Base62 strings correctly")
    void testIsValidBase62() {
        // Valid strings
        assertTrue(base62Encoder.isValidBase62("0"));
        assertTrue(base62Encoder.isValidBase62("123"));
        assertTrue(base62Encoder.isValidBase62("abc"));
        assertTrue(base62Encoder.isValidBase62("ABC"));
        assertTrue(base62Encoder.isValidBase62("1a2B3c"));
        assertTrue(base62Encoder.isValidBase62("aBcDeF123"));

        // Invalid strings
        assertFalse(base62Encoder.isValidBase62(null));
        assertFalse(base62Encoder.isValidBase62(""));
        assertFalse(base62Encoder.isValidBase62("@"));
        assertFalse(base62Encoder.isValidBase62("!"));
        assertFalse(base62Encoder.isValidBase62("123@"));
        assertFalse(base62Encoder.isValidBase62("abc-def"));
        assertFalse(base62Encoder.isValidBase62("test space"));
    }

    @Test
    @DisplayName("Should throw exception for invalid decode input")
    void testDecodeInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> base62Encoder.decode("@"));
        assertThrows(IllegalArgumentException.class, () -> base62Encoder.decode("abc!"));
        assertThrows(IllegalArgumentException.class, () -> base62Encoder.decode("test-123"));
    }

    @Test
    @DisplayName("Should return correct alphabet")
    void testGetAlphabet() {
        String expectedAlphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        assertEquals(expectedAlphabet, base62Encoder.getAlphabet());
    }

    @Test
    @DisplayName("Should handle edge cases for minimum length")
    void testEncodeWithMinLengthEdgeCases() {
        // Zero min length
        assertEquals("1", base62Encoder.encodeWithMinLength(1, 0));
        
        // Negative min length (should behave like 0)
        assertEquals("a", base62Encoder.encodeWithMinLength(10, -1));
        
        // Very large min length
        String result = base62Encoder.encodeWithMinLength(1, 20);
        assertEquals(20, result.length());
        assertTrue(result.endsWith("1"));
        assertTrue(result.startsWith("0000000000000000000"));
    }

    @Test
    @DisplayName("Should generate consistent results")
    void testConsistency() {
        // Test that multiple calls return the same result
        long testNumber = 12345;
        String first = base62Encoder.encode(testNumber);
        String second = base62Encoder.encode(testNumber);
        String third = base62Encoder.encode(testNumber);
        
        assertEquals(first, second);
        assertEquals(second, third);
        
        // Test decode consistency
        long firstDecode = base62Encoder.decode(first);
        long secondDecode = base62Encoder.decode(first);
        
        assertEquals(firstDecode, secondDecode);
        assertEquals(testNumber, firstDecode);
    }
}