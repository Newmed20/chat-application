package AppClient;
import javax.crypto.Cipher;
import javax.crypto.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Base64;

public class AESCipher {
    private static final String ALGORITHM = "AES";
    private Key secretKey;

    public AESCipher(String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(), ALGORITHM);
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



    public void generateSecretKey(String username) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // AES key size
        SecretKey secretKey = keyGen.generateKey();

        // Convert the generated key to byte array
        byte[] encodedKey = secretKey.getEncoded();

        // Encode the byte array into a Base64 string
        String encodedKeyString = Base64.getEncoder().encodeToString(encodedKey);

        // Update the secret key in the login table for the user in the database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "")) {
            String updateSql = "UPDATE login SET secretKey = ? WHERE Username = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                statement.setString(1, encodedKeyString);
                statement.setString(2, username);
                statement.executeUpdate();
            }
        }
        // Update the secret key in the current instance
        this.secretKey = new SecretKeySpec(encodedKey, ALGORITHM);
    }
}
