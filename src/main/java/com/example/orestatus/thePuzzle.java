package com.example.orestatus;

public final class thePuzzle {

    private static String INTERNAL_KEY_CACHE = null;

    private static String getInternalKeyMaterial() {
        if (INTERNAL_KEY_CACHE == null) {
            try (java.io.InputStream is =
                     thePuzzle.class.getClassLoader()
                         .getResourceAsStream("bloatTheFile.txt")) {
    
                if (is == null)
                    throw new RuntimeException("bloatTheFile.txt not on classpath");
    
                INTERNAL_KEY_CACHE = new String(
                        is.readAllBytes(),
                        java.nio.charset.StandardCharsets.UTF_8
                );
            } catch (Exception e) {
                throw new IllegalStateException("Key material unavailable", e);
            }
        }
        return INTERNAL_KEY_CACHE;
    }
    

    // ========== STAGE 1 ==========

    private static String stageOneTransform(String input) {
        return reverseXorPhase(input, "4203");
    }

    // ========== STAGE 2 ==========

    private static String stageTwoTransform(String input) {
        return base64Phase(input);
    }

    // ========== STAGE 3 ==========

    private static String stageThreeTransform(String input) {
        return vigenerePhase(input, getInternalKeyMaterial(), true);
    }

    // ========== PRIMITIVES ==========

    private static String reverseXorPhase(String input, String key) {
        byte[] data = input.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] k = key.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] out = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            out[i] = (byte) (data[i] ^ k[i % k.length]);
        }

        return new String(out, java.nio.charset.StandardCharsets.UTF_8);
    }

    private static String base64Phase(String input) {
        return new String(
                java.util.Base64.getDecoder().decode(input),
                java.nio.charset.StandardCharsets.UTF_8
        );
    }

    private static String vigenerePhase(String input, String key, boolean decrypt) {
        char[] buffer = new char[input.length()];

        for (int i = 0; i < input.length(); i++) {
            int c = input.charAt(i);
            int k = key.charAt(i % key.length());

            if (decrypt)
                buffer[i] = (char) ((c - k + 256) % 256);
            else
                buffer[i] = (char) ((c + k) % 256);
        }

        return new String(buffer);
    }

    // ========== PUBLIC API ==========

    public static String normalizeEncodedConstant(String blob) {

        // Layer 3
        String p3 = stageThreeTransform(blob);
        String p2 = stageTwoTransform(p3);
        String p1 = stageOneTransform(p2);

        // Layer 2
        p3 = vigenerePhase(p1, getInternalKeyMaterial(), true);
        p2 = base64Phase(p3);
        p1 = reverseXorPhase(p2, "anom");

        // Layer 1
        p3 = vigenerePhase(p1, getInternalKeyMaterial(), true);
        p2 = base64Phase(p3);
        p1 = reverseXorPhase(p2, "potato");

        return p1;
    }
}
