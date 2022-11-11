package com.alexelkin.pws_library_manager.core.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.util.Locale

@JacksonXmlRootElement(localName = "book")
data class Book(

  @JacksonXmlProperty(isAttribute = true, localName = "version")
  val version: String,

  @JacksonXmlProperty(isAttribute = true, localName = "name")
  val name: String,

  @JacksonXmlProperty(isAttribute = true, localName = "language")
  val language: Locale,

  val displayName: String,

  val displayShortName: String,

  val edition: BookEdition,

  val releaseDate: String,

  val description: String?,

  val preface: String?,

  val creators: List<String>?,

  val editors: List<String>?,

  @JacksonXmlProperty(localName = "psalms")
  val psalmRefs: List<String>
)
