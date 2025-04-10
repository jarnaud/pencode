package com.github.jarnaud.pencode.consumer;

import com.github.jarnaud.pencode.generator.Generator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

public class EncoderTest {

    @Test
    public void testSignatureProcess() {
        KeyPair kp = Generator.generateKeyPair();
        String signature = Encoder.sign("mon message", kp.getPrivate());
        Assertions.assertNotNull(signature);

        boolean res = Encoder.verify("mon message", signature, kp.getPublic());
        Assertions.assertTrue(res);
    }

    @Test
    public void testEncodingProcess() throws Exception {
        KeyPair pair = Generator.generateKeyPair();

        // Encode.
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pair.getPublic());

        String msg = "mon secret";
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        byte[] encBytes = cipher.doFinal(msgBytes);

        // Decode.
        Cipher c2 = Cipher.getInstance("RSA");
        c2.init(Cipher.DECRYPT_MODE, pair.getPrivate());
        byte[] decBytes = c2.doFinal(encBytes);
        String dec = new String(decBytes, StandardCharsets.UTF_8);

        Assertions.assertEquals(msg, dec);
    }
}
