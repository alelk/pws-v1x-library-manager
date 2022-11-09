package com.alexelkin.pws_library_manager.core.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.lang.IllegalArgumentException

enum class PsalmRefReason(@get: JsonValue val signature: String) {
  VARIATION("variation");

  companion object {
    @JsonCreator
    fun fromSignature(signature: String) = values().find { it.signature == signature } ?: throw IllegalArgumentException("unknown reason: $signature")
  }
}

data class PsalmRef(val reason: PsalmRefReason, val volume: Int, val edition: BookEdition, val number: Int)
