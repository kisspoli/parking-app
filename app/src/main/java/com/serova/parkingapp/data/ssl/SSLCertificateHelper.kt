package com.serova.parkingapp.data.ssl

import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

interface SSLCertificateHelper {
    fun createSSLContext(certificateResId: Int): Pair<SSLContext, X509TrustManager>
}