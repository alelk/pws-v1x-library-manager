package com.alexelkin.pws_library_manager.core.model

import com.alexelkin.pws_library_manager.core.util.NoArg
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver
import com.fasterxml.jackson.databind.jsontype.impl.MinimalClassNameIdResolver
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

private val objectMapper = jacksonObjectMapper()

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes(JsonSubTypes.Type(value = BibleRef::class, name = "bibleref"), JsonSubTypes.Type(value = PsalmRef::class, name = "psalmref"))
@JsonTypeIdResolver(ReferenceTypeIdResolver::class)
sealed interface Reference

class ReferenceTypeIdResolver(baseType: JavaType?, typeFactory: TypeFactory?, ptv: PolymorphicTypeValidator?) :
  MinimalClassNameIdResolver(baseType, typeFactory, ptv) {
  override fun typeFromId(context: DatabindContext?, id: String?): JavaType {
    return super.typeFromId(context, id)
  }
}

class ReferenceTypeResolver()


@JacksonXmlRootElement(localName = "bibleref")
class BibleRef : Reference {
  @get: JacksonXmlText
  var text: String = ""
  override fun toString(): String = "${BibleRef::class.simpleName}(text=$text)"
}

enum class PsalmRefReason(@get: JsonValue val signature: String) {
  VARIATION("variation");

  companion object {
    @JsonCreator
    fun fromSignature(signature: String) =
      values().find { it.signature == signature } ?: throw IllegalArgumentException("unknown reason: $signature")
  }
}

@JacksonXmlRootElement(localName = "psalmref")
data class PsalmRef(
  @get: JacksonXmlProperty(isAttribute = true, localName = "reason")
  val reason: PsalmRefReason,

  @get: JacksonXmlProperty(isAttribute = true, localName = "volume")
  val volume: Int,

  @get: JacksonXmlProperty(isAttribute = true, localName = "edition")
  val edition: BookEdition,

  @get: JacksonXmlProperty(isAttribute = true, localName = "number")
  val number: Int
) : Reference
