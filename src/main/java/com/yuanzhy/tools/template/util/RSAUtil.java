package com.yuanzhy.tools.template.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {
    /** LOG */
    private static final Logger LOG = LoggerFactory.getLogger(RSAUtil.class);
    /** 签名算法 */
    private static final String SIGN_ALGORITHMS = "SHA256WithRSA";
    /**
     * 随机生成密钥对
     *
     * @return 数组,0表示私钥, 1表示公钥
     * @throws NoSuchAlgorithmException
     */
    public static String[] genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString((privateKey.getEncoded()));
        return new String[]{privateKeyString, publicKeyString};
    }

    // ------------------------------ 加密解密 ------------------------------

    /**
     * RSA公钥加密
     *
     * @param publicKey  公钥
     * @param cleartext  明文
     * @return 密文base64字符串
     * @throws Exception 加密过程中的异常信息
     */
    public static String encryptToString(String publicKey, String cleartext) {
        //base64编码的公钥
        try {
            return Base64.getEncoder().encodeToString(encrypt(publicKey, cleartext.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA公钥加密
     *
     * @param publicKey  公钥
     * @param cleartext  明文
     * @return 密文数据
     * @throws Exception 加密过程中的异常信息
     */
    public static byte[] encrypt(String publicKey, String cleartext) {
        try {
            return encrypt(publicKey, cleartext.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA公钥加密
     *
     * @param publicKey  公钥
     * @param clearData  明文
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static byte[] encrypt(String publicKey, byte[] clearData) {
        try {
            byte[] decoded = Base64.getDecoder().decode(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(clearData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA私钥解密
     *
     * @param privateKey 私钥
     * @param ciphertext 密文base64字符串
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decryptToString(String privateKey, String ciphertext) {
        try {
            byte[] cipherData = Base64.getDecoder().decode(ciphertext.getBytes("UTF-8"));
            return new String(decrypt(privateKey, cipherData));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA私钥解密
     *
     * @param privateKey 私钥
     * @param ciphertext 密文base64字符串
     * @return 明文数据
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(String privateKey, String ciphertext) {
        try {
            return decrypt(privateKey, Base64.getDecoder().decode(ciphertext.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA私钥解密
     *
     * @param privateKey 私钥
     * @param cipherData 加密字符串
     * @return 明文数据
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(String privateKey, byte[] cipherData) {
        try {
            //base64编码的私钥
            byte[] decoded = Base64.getDecoder().decode(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return cipher.doFinal(cipherData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ------------------------------ 签名验签 ------------------------------

    /**
     * RSA签名
     *
     * @param privateKey 私钥
     * @param content    待签名数据
     * @return 签名值
     */
    public static String sign(String privateKey, String content) {
        try {
            return sign(privateKey, content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA签名
     *
     * @param privateKey 私钥
     * @param content    待签名数据
     * @return 签名值
     */
    public static String sign(String privateKey, byte[] content) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content);
            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA验签
     *
     * @param publicKey 公钥
     * @param content   待签名数据
     * @param sign      签名值
     * @return 布尔值
     */
    public static boolean verifySignature(String publicKey, String content, String sign) {
        try {
            return verifySignature(publicKey, content.getBytes("UTF-8"), sign);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA验签
     *
     * @param publicKey 公钥
     * @param content   待签名数据
     * @param sign      签名值
     * @return 布尔值
     */
    public static boolean verifySignature(String publicKey, byte[] content, String sign) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.getDecoder().decode(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content);
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            LOG.error("验证签名出错", e);
            return false;
        }
    }
}
