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
    private static final String ALGORITHM = "Blowfish";
    private static final Charset CHARSET = Charsets.UTF_8;
    private static final Companion Companion = new Companion();
    private static final String TRANSFORMATION = "Blowfish/ECB/NoPadding";
    private final String mKey;

    BlowfishDecrypter() {
        this.mKey = "n33cwrWZDhHbL2EdcEkF9rNGz9C22Ffx";
    }

    @NotNull
    final String decrypt(@NotNull String paramString)
            throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IllegalArgumentException {
        try {
            final SecretKeySpec key = new SecretKeySpec(this.mKey.getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] paramStringBytes = paramString.getBytes(Companion.getCHARSET());
            byte[] encrypt = Base64.decode(paramStringBytes, Base64.NO_WRAP);
            //        addPadding(paramStringBytes, localCipher.getBlockSize());
            byte[] result = cipher.doFinal(encrypt);
            //        return removeNullBytes(new String(paramStringBytes));
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
//
//    private byte[] addPadding(byte[] paramArrayOfByte, int paramInt) {
//        int i;
//        if (paramArrayOfByte.length % paramInt != 0) {
//            i = 1;
//            if (i == 0) {
////                break label53;
//            }
//        }
//        for (byte[] arrayOfByte = paramArrayOfByte; ; arrayOfByte = null) {
//            if (arrayOfByte != null) {
//                paramArrayOfByte = new byte[arrayOfByte.length + paramInt - arrayOfByte.length % paramInt];
//                System.arraycopy(arrayOfByte, 0, paramArrayOfByte, 0, arrayOfByte.length);
//            }
//            return paramArrayOfByte;
//            i = 0;
//            break;
//        }
//    }
//
//    private final String removeNullBytes(String paramString) {
//        Object localObject = Integer.valueOf(StringsKt.indexOf$default((CharSequence) paramString, '\000', 0, false, 6, null));
//        int i;
//        if (((Number) localObject).intValue() >= 0) {
//            i = 1;
//            if (i == 0) {
//                break label64;
//            }
//        }
//        for (; ; ) {
//            if (localObject != null) {
//                i = ((Number) localObject).intValue();
//                if (paramString == null) {
//                    throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
//                    i = 0;
//                    break;
//                    label64:
//                    localObject = null;
//                    continue;
//                }
//                localObject = paramString.substring(0, i);
//                if (localObject != null) {
//                    return localObject;
//                }
//            }
//        }
//        return paramString;
//        return (String) localObject;
//    }

    private static final class Companion {
        private Charset getCHARSET() {
            return BlowfishDecrypter.CHARSET;
        }
    }
}
