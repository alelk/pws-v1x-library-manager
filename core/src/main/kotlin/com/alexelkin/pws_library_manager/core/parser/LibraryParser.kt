package com.alexelkin.pws_library_manager.core.parser

import com.alexelkin.pws_library_manager.core.model.Book
import com.alexelkin.pws_library_manager.core.model.Library
import com.alexelkin.pws_library_manager.core.model.Psalm
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import java.io.File
import java.lang.IllegalStateException
import java.net.URI
import java.util.logging.Logger

class LibraryCtx(val library: Library, val xmlMapper: ObjectMapper, val libraryUri: URI) {

  private val log = Logger.getLogger(javaClass.name)

  private val booksByUri by lazy {
    val files = library.bookRefs.sortedByDescending { it.preference }.map { ref -> libraryUri.resolve(ref.bookRef) }
    files.map {
      try {
        it to xmlMapper.readValue(it.toURL(), Book::class.java)
      } catch (e: Throwable) {
        log.severe { "unable to parse book $it: ${e.message}" }
        throw e
      }
    }
  }

  val books: List<Book> by lazy { booksByUri.map { it.second } }

  val Book.psalms: List<Psalm>
    get() = booksByUri.find { it.second == this }?.let { (uri, book) ->
      val files = book.psalmRefs.map { ref -> uri.resolve(ref) }
      files.map {
        try {
          xmlMapper.readValue(it.toURL(), Psalm::class.java)
        } catch (e: Throwable) {
          log.severe { "unable to parse psalm $it: ${e.message}" }
          throw e
        }
      }
    } ?: throw IllegalStateException("Unable to load psalms of this book. Is this book from another library?")

}

class LibraryParser {

  val xmlMapper = XmlMapper(JacksonXmlModule()).registerModule(kotlinModule())

  fun <T> useLibrary(file: File, block: LibraryCtx.() -> T): T {
    val library = xmlMapper.readValue(file, Library::class.java)
    return block(LibraryCtx(library, xmlMapper, file.toURI()))
  }
}