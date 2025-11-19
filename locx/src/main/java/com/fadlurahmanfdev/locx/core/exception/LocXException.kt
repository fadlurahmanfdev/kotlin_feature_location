package com.fadlurahmanfdev.locx.core.exception

data class LocXException(
    val code: String,
    override val message: String
) : Throwable(message = message)
