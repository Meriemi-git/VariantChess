package fr.aboucorp.variantchess.app.utils;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import fr.aboucorp.variantchess.app.exceptions.HashException;

class Encryptor {

    public static String hash(String plainText) throws HashException {
        byte[] data = plainText.getBytes();
        KeyGenerator keygen = null;
        try {
            keygen = KeyGenerator.getInstance("AES");
            keygen.init(256);
            SecretKey key = keygen.generateKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] ciphertext = cipher.doFinal(data);
            byte[] iv = cipher.getIV();
            return Base64.encodeToString(iv, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            Log.e("fr.aboucorp.variantchess",e.getMessage());
            throw new HashException("Cannot hash text");
        } catch (BadPaddingException e) {
            Log.e("fr.aboucorp.variantchess",e.getMessage());
            throw new HashException("Cannot hash text");
        } catch (IllegalBlockSizeException e) {
            Log.e("fr.aboucorp.variantchess",e.getMessage());
            throw new HashException("Cannot hash text");
        }
    }
}
