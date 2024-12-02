package com.fadlurahmanfdev.feature_location.core.exception

data class FeatureLocationException(
    val code: String,
    override val message: String
) : Throwable(message = message)
