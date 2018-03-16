package happn.jordan.lazy.happntest;


import android.util.Base64;

import com.google.common.base.Charsets;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

class BlowfishDecrypter {
    private static final String HAPPN_ENCRYPTION_KEY = "n33cwrWZDhHbL2EdcEkF9rNGz9C22Ffx";
    private static final String ALGORITHM = "Blowfish";
    private static final Charset CHARSET = Charsets.UTF_8;
    private static final Companion Companion = new Companion();
    private static final String TRANSFORMATION = "Blowfish/ECB/NoPadding";
    private final String mKey;

    BlowfishDecrypter() {
        this.mKey = HAPPN_ENCRYPTION_KEY;
    }

    @NotNull
    final String decrypt(@NotNull String paramString)
            throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IllegalArgumentException {
        try {
            final SecretKeySpec key = new SecretKeySpec(this.mKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] paramStringBytes = paramString.getBytes(Companion.getCHARSET());
            byte[] encrypt = Base64.decode(paramStringBytes, Base64.NO_WRAP);
            byte[] result = cipher.doFinal(encrypt);
            return new String(result);
        } catch (Exception ignored) {
        }

        return "";
    }

    private static final class Companion {
        private Charset getCHARSET() {
            return BlowfishDecrypter.CHARSET;
        }
    }
}
