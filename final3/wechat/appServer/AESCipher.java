package appServer;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class AESCipher {
    private static final String ALGORITHM = "AES";
    private Key secretKey;

    public AESCipher(String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(), ALGORITHM);
    }
    public byte[] encryptf(byte[] data) {
        try {
            if (secretKey != null) {
                Cipher cipher = Cipher.getInstance("AES"); // Specify padding
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                return cipher.doFinal(data);
            } else {
                System.err.println("Secret key is null.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decryptf(byte[] encryptedData) {
        try {
            if (secretKey != null) {
                Cipher cipher = Cipher.getInstance("AES"); // Specify padding
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                return cipher.doFinal(encryptedData);
            } else {
                System.err.println("Secret key is null.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String encrypt(String dataToEncrypt) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(dataToEncrypt.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }
}
