package com.alexelkin.pws_library_manager.core.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.util.StdConverter
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

data class PsalmText(
  @JacksonXmlProperty(localName = "verse")
  @JacksonXmlElementWrapper(useWrapping = false)
  val verses: List<PsalmVerse>,

  @JacksonXmlProperty(localName = "chorus")
  @JacksonXmlElementWrapper(useWrapping = false)
  val choruses: List<PsalmChorus>
)

sealed interface PsalmPart {
  val numbers: List<Int>
  val text: String
}

class NumberListConverter : StdConverter<String, List<Int>>() {
  override fun convert(value: String?): List<Int> =
    value?.split(',')?.mapNotNull { it.trim().ifBlank { null } }?.map { it.toInt() } ?: emptyList()
}

@JacksonXmlRootElement(localName = "verse")
data class PsalmVerse(
  @get: JacksonXmlProperty(localName = "number", isAttribute = true)
  @get: JsonDeserialize(converter = NumberListConverter::class)
  override val numbers: List<Int>
) : PsalmPart {
  @get: JacksonXmlText
  override var text: String = ""

  override fun toString(): String = "${javaClass.simpleName}(numbers=${numbers}, text=${text.replace("\n", "\\n")})"
}

@JacksonXmlRootElement(localName = "chorus")
data class PsalmChorus(
  @get: JacksonXmlProperty(localName = "number", isAttribute = true)
  @get: JsonDeserialize(converter = NumberListConverter::class)
  override val numbers: List<Int>
) : PsalmPart {
  @get: JacksonXmlText
  override var text: String = ""

  override fun toString(): String = "${javaClass.simpleName}(numbers=${numbers}, text=${text.replace("\n", "\\n")})"
}