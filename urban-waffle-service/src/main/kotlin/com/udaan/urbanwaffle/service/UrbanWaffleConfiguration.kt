package com.udaan.urbanwaffle.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.dropwizard.Configuration

@JsonIgnoreProperties(ignoreUnknown = true)
class UrbanWaffleConfiguration : Configuration()
