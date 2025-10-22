package com.example.demo;

import com.example.demo.util.AESEncryptionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestEncryption {

    @Autowired
    AESEncryptionUtil aes;

    @Test
    void encryptDecryptWorks() throws Exception {
        String text = "4111111111111234";
        String encrypted = aes.encrypt(text);
        String decrypted = aes.decrypt(encrypted);

        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
    }
}
