package com.alelk.pws.library_manager

import cats.data.Validated.Valid
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*
import com.github.dwickern.macros.NameOf.*
import io.lemonlabs.uri.Url
import cats.implicits.*

class PwsLibraryV1xLoaderTest extends AnyFlatSpec with should.Matchers with PwsLibraryV1xLoader {

  nameOf(withLibrary) can "load library content" in {
    val libFile =
      Url.parse(
        classOf[PwsLibraryV1xLoaderTest].getClassLoader
          .getResource("library-examples-full/content-ru.pwslib").toString)
    val Valid((resultPsalm, psalmInfo)) = withLibrary(libFile) { lib =>
      lib.info.version.major shouldBe 1
      lib.info.version.minor shouldBe 2
      lib.books.andThen { books =>
        books should have length 2
        val List(book1, book2) = books
        book1.info.displayName shouldNot have length 0
        book2.info.displayName shouldNot have length 0
        book2.psalms.andThen { psalms =>
          psalms should have length 2
          psalms.head.valid
        }
      }
    }
    resultPsalm.name shouldNot have length 0
  }

}
