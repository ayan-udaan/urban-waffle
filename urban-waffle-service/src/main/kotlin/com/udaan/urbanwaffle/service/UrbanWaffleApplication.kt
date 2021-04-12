package com.udaan.urbanwaffle.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.udaan.urbanwaffle.service.resources.SampleResource
import com.udaan.common.server.DropwizardApplication
import com.udaan.common.server.register
import com.udaan.resources.lifecycle.LifeCycleObjectRepo
import io.dropwizard.forms.MultiPartBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper
import io.dropwizard.jersey.setup.JerseyEnvironment


class UrbanWaffleApplication : DropwizardApplication<UrbanWaffleConfiguration>() {

    override fun getGuiceModules(configuration: UrbanWaffleConfiguration, environment: Environment) = listOf(
            UrbanWaffleServiceModule()
    )

    override fun getDropwizardBundles() = listOf(MultiPartBundle())

    override fun getJacksonModules() = listOf(JavaTimeModule())

    override fun getResourceClasses() = listOf(
            SampleResource::class.java
    )

    override fun initializeAdditional(bootstrap: Bootstrap<UrbanWaffleConfiguration>) {
        bootstrap.objectMapper
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
    }

    override fun runAdditional(configuration: UrbanWaffleConfiguration, environment: Environment) {
        environment.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        environment.register(LifeCycleObjectRepo.global())

        environment.jersey().registerExceptionMappers()
    }

    private fun JerseyEnvironment.registerExceptionMappers() {
        register(JsonProcessingExceptionMapper(true))
    }
}

fun main(args: Array<String>) {
    UrbanWaffleApplication().startWithArgs(args)
}
