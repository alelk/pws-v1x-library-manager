package com.alelk.pws.library_manager
package xml_support

import model.*

import advxml.data.ValidatedNelThrow
import advxml.implicits.*
import cats.data.Validated.Valid
import com.github.dwickern.macros.NameOf.*
import io.lemonlabs.uri.RelativeUrl
import org.scalactic.PrettyPair
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

import java.util.Locale
import scala.xml.PrettyPrinter

class LibraryInfoXmlConverterTest extends AnyFlatSpec with should.Matchers with LibraryInfoXmlConverter {

  nameOf(libraryInfoXmlDecoder) should "parse library info from XML content" in {
    val xml =
    // @formatter:off
      <pwslibrary version='1.2'>
        <books>
          <ref preference='40'>books/book2.pwsbk</ref>
          <ref preference='25'>books/book1.pwsbk</ref>
        </books>
      </pwslibrary>
      // @formatter:on

    val Valid(libInfo) = xml.decode[LibraryInfo]
    libInfo.version shouldBe Version("1.2")
    libInfo.bookRefs should have length 2
    val List(ref1, ref2) = libInfo.bookRefs
    ref1.preference shouldBe 40
    ref1.reference shouldBe RelativeUrl.parse("books/book2.pwsbk")
    ref2.preference shouldBe 25
    ref2.reference shouldBe RelativeUrl.parse("books/book1.pwsbk")
  }

  nameOf(libraryInfoXmlEncoder) should "serialize library info to XML" in {
    val actual =
      LibraryInfo(
        version = Version("1.2"),
        bookRefs = List(
          BookRef(40, RelativeUrl.parse("books/book2.pwsbk")),
          BookRef(25, RelativeUrl.parse("books/book1.pwsbk")),
        )).encode

    val expected =
    // @formatter:off
      <pwslibrary version='1.2'>
        <books>
          <ref preference='40'>books/book2.pwsbk</ref>
          <ref preference='25'>books/book1.pwsbk</ref>
        </books>
      </pwslibrary>
    // @formatter:on
    actual.normalize.toString() shouldBe expected.normalize.toString()
  }

  classOf[LibraryInfoXmlConverter].getSimpleName should "convert library info to XML and parse it back" in {
    val expected =
      LibraryInfo(
        version = Version("1.2"),
        bookRefs = List(
          BookRef(40, RelativeUrl.parse("books/book2.pwsbk")),
          BookRef(25, RelativeUrl.parse("books/book1.pwsbk")),
        ))
    val Valid(actual) = expected.encode.normalize.decode[LibraryInfo]
    actual shouldBe expected
  }
}
