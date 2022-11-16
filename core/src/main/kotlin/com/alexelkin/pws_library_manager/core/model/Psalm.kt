package com.alexelkin.pws_library_manager.core.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
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
  val references: References?,

  @get: JacksonXmlProperty(localName = "author")
  val author: String?,

  @get: JacksonXmlProperty(localName = "translator")
  val translator: String?,

  @get: JacksonXmlProperty(localName = "composer")
  val composer: String?,

  @get: JacksonXmlProperty(localName = "text")
  val text: PsalmText
)

data class References(
//  @JacksonXmlProperty(localName = "bibleref")

  @JacksonXmlElementWrapper(useWrapping = true, localName = "bibleref")
  val bibleReferences: List<BibleRef>?,

//  @JacksonXmlProperty(localName = "psalmref")
//  @JacksonXmlElementWrapper(useWrapping = false)
//  val psalmReferences: List<PsalmRef>
)
