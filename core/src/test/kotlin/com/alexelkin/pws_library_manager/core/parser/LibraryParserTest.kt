package com.alexelkin.pws_library_manager.core.parser

import com.alexelkin.pws_library_manager.core.model.BookEdition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.lang.IllegalStateException

class LibraryParserTest : StringSpec({

  "library parser can load library tree" {
    val p = LibraryParser()
    p.useLibrary(
      File(
        LibraryParser::class.java.getResource("/library-examples-full/content-ru.pwslib")?.file
          ?: throw IllegalArgumentException("no such file")
      )
    ) {
      println("library uri: $libraryUri")
      library.bookRefs.size shouldBe 2
      val b = books.find { it.edition == BookEdition.PV3300 } ?: throw IllegalStateException("no book found")
      b.psalms.size shouldBe 5
    }
  }
})