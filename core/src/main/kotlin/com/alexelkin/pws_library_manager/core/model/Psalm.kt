package com.alexelkin.pws_library_manager.core.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "psalm")
@JsonPropertyOrder("version", "name", "numbers", "tonalities")
data class Psalm(
  @get: JacksonXmlProperty(isAttribute = true, localName = "version")
  val version: String,

  @get: JacksonXmlProperty(isAttribute = true, localName = "name")
  val name: String,

  @get: JacksonXmlProperty(localName = "numbers")
  val numbers: List<PsalmNumber>,

  @get: JacksonXmlProperty(localName = "tonalities")
  val tonalities: List<String>?,

  @get: JacksonXmlProperty(localName = "references")
  val references: List<PsalmRef>?,

  @get: JacksonXmlProperty(localName = "text")
  val text: PsalmText
)
