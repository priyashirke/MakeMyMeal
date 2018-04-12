package mechsoft.makemymeal;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * Created by priyanka on 12-04-2018.
 */

public class TripleDES {
    String secretKey = "YOUR_KEY";
    public String _encrypt(String message) throws Exception {

        MessageDigest md = MessageDigest.getInstance("md5");
        byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
        byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        KeySpec keySpec = new DESedeKeySpec(keyBytes);
        SecretKey key = SecretKeyFactory.getInstance("DESede").generateSecret(keySpec);

        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] plainTextBytes = message.getBytes("utf-8");
        byte[] buf = cipher.doFinal(plainTextBytes);
        byte[] base64Bytes = Base64.encode(buf,Base64.DEFAULT);
        String base64EncryptedString = new String(base64Bytes);

        return base64EncryptedString;
    }

    public String _decrypt(String encryptedText) throws Exception {

        byte[] message = Base64.decode(encryptedText.getBytes("utf-8"),Base64.DEFAULT);
        MessageDigest md = MessageDigest.getInstance("md5");
        byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
        byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        KeySpec keySpec = new DESedeKeySpec(keyBytes);
        SecretKey key = SecretKeyFactory.getInstance("DESede").generateSecret(keySpec);

        Cipher decipher = Cipher.getInstance("DESede");
        decipher.init(Cipher.DECRYPT_MODE, key);

        byte[] plainText = decipher.doFinal(message);

        String result = new String(plainText, "UTF-8");

        return result;
    }
}
