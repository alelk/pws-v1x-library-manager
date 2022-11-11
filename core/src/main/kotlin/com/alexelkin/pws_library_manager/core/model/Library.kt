package com.alexelkin.pws_library_manager.core.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

data class BookReference(
  @field: JacksonXmlProperty(isAttribute = true, localName = "preference")
  val preference: Int
) {
  @field: JacksonXmlText
  val bookRef: String = ""
}

@JacksonXmlRootElement(localName = "pwslibrary")
data class Library(
  @JacksonXmlProperty(isAttribute = true, localName = "version")
  val version: String,

  @JacksonXmlProperty(localName = "books")
  val bookRefs: List<BookReference>
)
