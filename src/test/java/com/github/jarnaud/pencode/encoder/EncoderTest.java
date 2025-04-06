package com.github.jarnaud.pencode.encoder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

@SpringBootTest(classes = {Encoder.class})
public class EncoderTest {

    @Autowired
    private Encoder encoder;

    @Test
    public void testEncodingProcess() throws Exception {
        KeyPair pair = encoder.generateKeyPair();

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
