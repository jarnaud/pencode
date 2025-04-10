package com.github.jarnaud.pencode.consumer;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

@Slf4j
public class Encoder {

    private static final String SIGNATURE_ALGO = "SHA256WithRSA";
    private static final String ALGO = "RSA";

    /**
     * Return the message signature in Base64.
     *
     * @param message    the message.
     * @param privateKey the private key.
     * @return the message signature in Base64.
     */
    public static String sign(String message, PrivateKey privateKey) {
        try {
            Signature sig = Signature.getInstance(SIGNATURE_ALGO);
            sig.initSign(privateKey);
            byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
            sig.update(msgBytes);
            return Base64.getEncoder().encodeToString(sig.sign());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String message, String signature, PublicKey publicKey) {
        try {
            Signature sig = Signature.getInstance(SIGNATURE_ALGO);
            sig.initVerify(publicKey);
            byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
            sig.update(msgBytes);

            byte[] sigBytes = Base64.getDecoder().decode(signature);
            return sig.verify(sigBytes);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encode a message with the provided public key.
     *
     * @param message   the message.
     * @param publicKey the public key.
     * @return the encoded message.
     */
    public byte[] encode(String message, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
            return cipher.doFinal(msgBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
