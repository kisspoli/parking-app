package com.serova.parkingapp.data.api.interceptor

import android.content.Context
import android.util.Log
import com.serova.parkingapp.R
import com.serova.parkingapp.domain.repository.LanguageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AppInfoInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val languageRepository: LanguageRepository
) : Interceptor {

    private val tag = this.javaClass.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        val packageManager = context.packageManager
        val packageName = context.packageName
        val platform = "android"
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        val language = languageRepository.getCurrentLanguage()
        val languageName = if (language.localeCode.isEmpty()) {
            context.resources.configuration.locales.get(0).language
        } else {
            language.localeCode
        }
        val accept = "application/json"

        val newRequest = chain.request().newBuilder()
            .header("appName", context.getString(R.string.app_name))
            .header("platform", platform)
            .header("appVersion", "$versionName")
            .header("appLang", languageName)
            .header("Accept", accept)
            .build()

        Log.i(
            tag, """
                Headers added:
                - appName: ${context.getString(R.string.app_name)}
                - platform: $platform
                - appVersion: $versionName
                - appLang: $languageName
                - Accept: $accept
            """.trimIndent()
        )

        return chain.proceed(newRequest)
    }
}