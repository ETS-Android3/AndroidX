package com.androidx.net.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Map;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Author: Relin
 * Describe:证书信任管理
 * Date:2020/5/25 12:04
 */
public class HttpsTrustManager {

    /**
     * 证书流
     */
    private Map<String, InputStream> certificates;
    /**
     * 信任管理器工厂
     */
    private TrustManagerFactory trustManagerFactory;

    public HttpsTrustManager() {

    }

    public HttpsTrustManager(Map<String, InputStream> certificates) {
        this.certificates = certificates;
        initTrustManager();
    }

    /**
     * 初始化信任管理器
     */
    private void initTrustManager() {
        if (certificates == null || certificates.size() == 0) {
            return;
        }
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            for (String alias : certificates.keySet()) {
                InputStream is = certificates.get(alias);
                keyStore.setCertificateEntry(alias, certificateFactory.generateCertificate(is));
                is.close();
            }
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取信任管理器
     *
     * @return
     */
    public TrustManager[] getTrustManager() {
        if (trustManagerFactory == null) {
            return new TrustManager[]{new HttpsX509TrustManager()};
        }
        return trustManagerFactory.getTrustManagers();
    }

    /**
     * 获取证书Map
     *
     * @return
     */
    public Map<String, InputStream> certificates() {
        return certificates;
    }

}
