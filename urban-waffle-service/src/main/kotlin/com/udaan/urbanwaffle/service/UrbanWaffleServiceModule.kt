package com.udaan.urbanwaffle.service

import com.google.inject.AbstractModule
import com.udaan.urbanwaffle.core.UrbanWaffleCoreModule

class UrbanWaffleServiceModule() : AbstractModule() {
    override fun configure() {
        install(UrbanWaffleCoreModule())
    }
}
