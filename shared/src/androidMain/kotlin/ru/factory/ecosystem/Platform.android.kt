package ru.factory.ecosystem

import android.content.Context
import android.content.Intent
import android.os.Build
import com.chuckerteam.chucker.api.Chucker
import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override fun openInspector() {
        appContext?.let {
            val intent = Chucker.getLaunchIntent(it)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            it.startActivity(intent)
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()

private var appContext: Context? = null

fun initPlatform(context: Context) {
    appContext = context.applicationContext
}

actual fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    engine {
        appContext?.let {
            addInterceptor(ChuckerInterceptor.Builder(it).build())
        }
    }
}