package com.alexelkin.pws_library_manager.core.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

@JacksonXmlRootElement(localName = "number")
data class PsalmNumber(
  @field: JacksonXmlProperty(isAttribute = true, localName = "edition")
  val edition: BookEdition
) {
  @JacksonXmlText
  var number: Int = -1

  override fun toString(): String = "${javaClass.simpleName}(edition=${edition.signature}, number=${number})"
}
