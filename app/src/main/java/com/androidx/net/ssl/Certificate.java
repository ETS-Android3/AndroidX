package com.androidx.net.ssl;

import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Author: Relin
 * Describe: Https证书
 * Date:2020/11/30 19:37
 */
public class Certificate {

    /**
     * Transport Layer Security，传输层安全协议
     */
    public static final String TSL = "TSL";

    /**
     * Secure Socket Layer，安全套接字层
     */
    public static final String SSL = "SSL";

    /**
     * 协议，SSL,TSL
     */
    public final String protocol;
    /**
     * 证书
     */
    public final Map<String, InputStream> certificates;

    /**
     * 套接字上下文
     */
    private SSLContext sslContext;

    /**
     * 信任管理器
     */
    private TrustManager[] trustManagers;


    public Certificate(Builder builder) {
        this.protocol = builder.protocol;
        this.certificates = builder.certificates;
        buildCertificates();
    }

    /**
     * 套接字上下文
     *
     * @return
     */
    public SSLContext getSSLContext() {
        return sslContext;
    }

    /**
     * 套接字工厂
     *
     * @return
     */
    public SSLSocketFactory getSSLSocketFactory() {
        return sslContext.getSocketFactory();
    }

    /**
     * 信任管理器
     *
     * @return
     */
    public TrustManager[] getTrustManagers() {
        return trustManagers;
    }

    /**
     * 构建证书
     */
    public void buildCertificates() {
        try {
            sslContext = SSLContext.getInstance(protocol);
            if (certificates == null || certificates.size() == 0) {
                trustManagers = new HttpsTrustManager().getTrustManager();
            } else {
                trustManagers = new HttpsTrustManager(certificates).getTrustManager();
            }
            sslContext.init(null, trustManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HttpsHostnameVerifier());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static class Builder {

        /**
         * 协议，SSL,TSL
         */
        private String protocol = "SSL";

        /**
         * 证书
         */
        private Map<String, InputStream> certificates;


        public String protocol() {
            return protocol;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Map<String, InputStream> certificates() {
            return certificates;
        }

        public Builder certificates(Map<String, InputStream> certificates) {
            this.certificates = certificates;
            return this;
        }

        public Builder addCertificate(String alias, InputStream inputStream) {
            if (certificates == null) {
                certificates = new HashMap<>();
            }
            certificates.put(alias, inputStream);
            return this;
        }

        public Certificate build() {
            return new Certificate(this);
        }

    }

}
