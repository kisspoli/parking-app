package com.serova.parkingapp.data.ssl

import android.content.Context
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class SSLCertificateHelperImpl @Inject constructor(
    private val context: Context
) : SSLCertificateHelper {

    override fun createSSLContext(certificateResId: Int): Pair<SSLContext, X509TrustManager> {
        val certificateInputStream = context.resources.openRawResource(certificateResId)
        val certificate = loadCertificate(certificateInputStream)

        val keyStore = createKeyStore(certificate)

        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(keyStore)
        val trustManagers = trustManagerFactory.trustManagers
        val x509TrustManager = trustManagers[0] as X509TrustManager

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagers, null)

        return Pair(sslContext, x509TrustManager)
    }

    private fun loadCertificate(inputStream: InputStream): Certificate {
        return try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            certificateFactory.generateCertificate(inputStream)
        } finally {
            inputStream.close()
        }
    }

    private fun createKeyStore(certificate: Certificate): KeyStore {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", certificate)
        return keyStore
    }
}