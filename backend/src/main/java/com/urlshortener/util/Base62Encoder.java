package com.urlshortener.util;

import org.springframework.stereotype.Component;

/**
 * Base62 encoder utility for generating short codes
 * Uses characters: 0-9, a-z, A-Z (62 characters total)
 */
@Component
public class Base62Encoder {
    
    private static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;
    
    /**
     * Encode a number to Base62 string
     * @param number the number to encode
     * @return Base62 encoded string
     */
    public String encode(long number) {
        if (number == 0) {
            return String.valueOf(BASE62_ALPHABET.charAt(0));
        }
        
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.append(BASE62_ALPHABET.charAt((int) (number % BASE)));
            number /= BASE;
        }
        
        return result.reverse().toString();
    }
    
    /**
     * Decode a Base62 string to number
     * @param encoded the Base62 encoded string
     * @return decoded number
     */
    public long decode(String encoded) {
        long result = 0;
        long power = 1;
        
        for (int i = encoded.length() - 1; i >= 0; i--) {
            char character = encoded.charAt(i);
            int value = BASE62_ALPHABET.indexOf(character);
            
            if (value == -1) {
                throw new IllegalArgumentException("Invalid character in Base62 string: " + character);
            }
            
            result += value * power;
            power *= BASE;
        }
        
        return result;
    }
    
    /**
     * Generate a short code with minimum length
     * @param number the number to encode
     * @param minLength minimum length of the result
     * @return padded Base62 encoded string
     */
    public String encodeWithMinLength(long number, int minLength) {
        String encoded = encode(number);
        
        // Pad with leading zeros if necessary
        while (encoded.length() < minLength) {
            encoded = BASE62_ALPHABET.charAt(0) + encoded;
        }
        
        return encoded;
    }
    
    /**
     * Check if a string is valid Base62
     * @param str the string to validate
     * @return true if valid Base62, false otherwise
     */
    public boolean isValidBase62(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        
        for (char c : str.toCharArray()) {
            if (BASE62_ALPHABET.indexOf(c) == -1) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get the alphabet used for Base62 encoding
     * @return the Base62 alphabet string
     */
    public String getAlphabet() {
        return BASE62_ALPHABET;
    }
}