package com.chyiiiiiiiiiiiiii.zendesk_messaging

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

/**
 * The app context handed to the Zendesk SDK, speaking the app's own language.
 *
 * The SDK reads the user's locale from its app context's configuration and writes it
 * over the Zendesk user's locale on every user refresh, so an app that picks its
 * language in-app would otherwise report the phone's language to Zendesk.
 */
class AppLocaleContext(base: Context) : ContextWrapper(base) {
    @Volatile
    var locale: Locale? = null

    @Volatile
    private var localized: Pair<Configuration, Resources>? = null

    // The SDK swaps the context it is given for its app context: this keeps the wrapper.
    override fun getApplicationContext(): Context = this

    override fun getResources(): Resources {
        val resources = super.getResources()
        val locale = locale ?: return resources
        val config = Configuration(resources.configuration).apply { setLocale(locale) }
        localized?.let { (cached, localizedResources) ->
            if (cached == config) return localizedResources
        }
        return createConfigurationContext(config).resources.also { localized = config to it }
    }
}
