package com.nice.mqtt.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @Author hzdz163@163.com
 * @Description token 生成工具类 https://open.iot.10086.cn/doc/mqtt/book/manual/auth/java.html
 * @Date 11:24 2020/9/2
 * @Param
 * @return
 **/
public class OneNetTokenUtil {

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
      String token =   generateDefaultProductToken("xxxx","xxxxx");
        System.out.println(token);
    }

    /**
     * 默认版本信息
     */
    private static String defaultVersion = "2018-10-31";

    /**
     * 默认方法
     */
    private static SignatureMethod defaultSignatureMethod = SignatureMethod.SHA1;


    /**
     * 默认生成设备访问的token
     *
     * @param productId
     * @param deviceName
     * @param accessKey
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String generateDefaultDeviceToken(String productId, String deviceName, String accessKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String resourceName = "products/".concat(productId).concat("/devices/").concat(deviceName);
        String token;
        token = assembleToken(defaultVersion, resourceName, defaultExpirationTime(), SignatureMethod.SHA1.name().toLowerCase(), accessKey);
        return token;
    }

    /**
     * 生成设备访问的token
     *
     * @param version
     * @param productId
     * @param deviceName
     * @param tokenValue
     * @param tokenUnit
     * @param accessKey
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String generateDeviceToken(String version, String productId, String deviceName, Integer tokenValue, String tokenUnit, String accessKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String resourceName = "products/".concat(productId).concat("/devices/").concat(deviceName);
        String token;
        token = assembleToken(version, resourceName, expirationTime(tokenValue, tokenUnit), SignatureMethod.SHA1.name().toLowerCase(), accessKey);
        return token;
    }

    /**
     * 生成连接Onenet的token
     *
     * @param version
     * @param productId
     * @param expirationTime
     * @param signatureMethod
     * @param accessKey
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String generateProductToken(String version, String productId, String expirationTime, String signatureMethod, String accessKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String token;
        String resourceName = "products/" + productId;
        token = assembleToken(version, resourceName, expirationTime, signatureMethod, accessKey);
        return token;
    }

    /**
     * 生成连接Onenet的token
     *
     * @param productId
     * @param expirationTime
     * @param accessKey
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String generateProductToken(String productId, String expirationTime, String accessKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return generateProductToken(defaultVersion, productId, expirationTime, defaultSignatureMethod.toString().toLowerCase(), accessKey);
    }

    /**
     * 默认生成连接Onenet的token
     *
     * @param productId
     * @param accessKey
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String generateDefaultProductToken(String productId, String accessKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return generateProductToken(defaultVersion, productId, defaultExpirationTime(), defaultSignatureMethod.toString().toLowerCase(), accessKey);
    }

    public static String assembleToken(String version, String resourceName, String expirationTime, String signatureMethod, String accessKey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder sb = new StringBuilder();
        String res = URLEncoder.encode(resourceName, "UTF-8");
        String sig = URLEncoder.encode(generatorSignature(version, resourceName, expirationTime
                , accessKey, signatureMethod), "UTF-8");
        sb.append("version=")
                .append(version)
                .append("&res=")
                .append(res)
                .append("&et=")
                .append(expirationTime)
                .append("&method=")
                .append(signatureMethod)
                .append("&sign=")
                .append(sig);
        return sb.toString();
    }

    public static String defaultAssembleToken(String resourceName, String accessKey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder sb = new StringBuilder();
        String res = URLEncoder.encode(resourceName, "UTF-8");
        String sig = URLEncoder.encode(generatorSignature(defaultVersion, resourceName, defaultExpirationTime()
                , accessKey, defaultSignatureMethod.name().toLowerCase()), "UTF-8");
        sb.append("version=")
                .append(defaultVersion)
                .append("&res=")
                .append(res)
                .append("&et=")
                .append(defaultExpirationTime())
                .append("&method=")
                .append(defaultSignatureMethod.name().toLowerCase())
                .append("&sign=")
                .append(sig);
        return sb.toString();
    }


    public static String generatorSignature(String version, String resourceName, String expirationTime, String accessKey, String signatureMethod)
            throws NoSuchAlgorithmException, InvalidKeyException {
        String encryptText = expirationTime + "\n" + signatureMethod + "\n" + resourceName + "\n" + version;
        String signature;
        byte[] bytes = HmacEncrypt(encryptText, accessKey, signatureMethod);
        signature = Base64.getEncoder().encodeToString(bytes);
        return signature;
    }

    public static byte[] HmacEncrypt(String data, String key, String signatureMethod)
            throws NoSuchAlgorithmException, InvalidKeyException {
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKeySpec signinKey = null;
        signinKey = new SecretKeySpec(Base64.getDecoder().decode(key),
                "Hmac" + signatureMethod.toUpperCase());

        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = null;
        mac = Mac.getInstance("Hmac" + signatureMethod.toUpperCase());

        //用给定密钥初始化 Mac 对象
        mac.init(signinKey);

        //完成 Mac 操作
        return mac.doFinal(data.getBytes());
    }


    /**
     * 默认计算token超时时间
     *
     * @return
     */
    public static String defaultExpirationTime() {
        try {
            long expirationTime = 3600 * 24 + System.currentTimeMillis() / 1000;
            return String.valueOf(expirationTime);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 计算token超时时间
     *
     * @param tokenValue
     * @param tokenUnit
     * @return
     */
    public static String expirationTime(Integer tokenValue, String tokenUnit) {
        try {
            Integer time = OneNetTokenEnum.valueOf(tokenUnit).getNumber();
            long expirationTime = time * tokenValue + System.currentTimeMillis() / 1000;
            return String.valueOf(expirationTime);
        } catch (Exception e) {
            return null;
        }
    }

    public enum SignatureMethod {
        SHA1, MD5, SHA256;
    }

    public enum OneNetTokenEnum {
        SECONDS(1),
        MINUTE(60),
        HOUR(60 * 60),
        DAY(60 * 60 * 24),
        MONTH(60 * 60 * 24 * 30);

        private Integer number;

        private OneNetTokenEnum(Integer number) {
            this.number = number;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }
    }

}
