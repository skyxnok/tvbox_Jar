package com.github.catvod.spider;

import android.util.Base64;

import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;

import java.security.MessageDigest;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class JUtil {

    public static String get(String url, Map<String, String> headers) {
        return OkHttp.string(url, headers);
    }

    public static String post(String url, String body, Map<String, String> headers) {
        OkResult res = OkHttp.post(url, body, headers);
        return res == null ? "" : res.getBody();
    }

    public static String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return hex(md.digest(text.getBytes("UTF-8")));
        } catch (Exception e) {
            return "";
        }
    }

    public static String md5Base64Url(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return Base64.encodeToString(md.digest(text.getBytes("UTF-8")), Base64.NO_WRAP)
                    .replace("=", "").replace("+", "-").replace("/", "_");
        } catch (Exception e) {
            return "";
        }
    }

    public static String sha1(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return hex(md.digest(text.getBytes("UTF-8")));
        } catch (Exception e) {
            return "";
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public static String aesDecrypt(String data, String key, String iv) {
        try {
            byte[] raw = Base64.decode(data, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), "AES"), new IvParameterSpec(iv.getBytes("UTF-8")));
            return new String(cipher.doFinal(raw), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static String aesEncryptEcb(String data, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), "AES"));
            return Base64.encodeToString(cipher.doFinal(data.getBytes("UTF-8")), Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }

    public static String aesEncrypt(String data, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), "AES"), new IvParameterSpec(iv.getBytes("UTF-8")));
            return Base64.encodeToString(cipher.doFinal(data.getBytes("UTF-8")), Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }

    public static String aesEncryptHex(String data, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), "AES"), new IvParameterSpec(iv.getBytes("UTF-8")));
            return hex(cipher.doFinal(data.getBytes("UTF-8"))).toUpperCase();
        } catch (Exception e) {
            return "";
        }
    }

    public static String aesDecryptHexCbc(String dataHex, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), "AES"), new IvParameterSpec(iv.getBytes("UTF-8")));
            return new String(cipher.doFinal(hexToBytes(dataHex)), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static String aesDecryptEcb(String data, String key) {
        try {
            byte[] raw = Base64.decode(data, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), "AES"));
            return new String(cipher.doFinal(raw), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static String rsaDecrypt(String data, String privateKeyPem) {
        try {
            String pem = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.decode(pem, Base64.DEFAULT);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.decode(data, Base64.DEFAULT)), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static String rsaEncrypt(String data, String publicKeyPem) {
        try {
            String pem = publicKeyPem.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.decode(pem, Base64.DEFAULT);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeToString(cipher.doFinal(data.getBytes("UTF-8")), Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }

    public static String sha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return hex(md.digest(text.getBytes("UTF-8")));
        } catch (Exception e) {
            return "";
        }
    }

    public static String aesGcmDecrypt(String hexBody, String key) {
        try {
            byte[] all = hexToBytes(hexBody);
            byte[] iv = new byte[12];
            System.arraycopy(all, 0, iv, 0, 12);
            byte[] ct = new byte[all.length - 12];
            System.arraycopy(all, 12, ct, 0, ct.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), "AES"), new GCMParameterSpec(128, iv));
            return new String(cipher.doFinal(ct), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    private static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
