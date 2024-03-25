package com.alelk.pws.library_manager
package xml_support

import model.*

import advxml.data.ValidatedNelThrow
import advxml.implicits.*
import cats.data.Validated.Valid
import com.github.dwickern.macros.NameOf.*
import io.lemonlabs.uri.RelativeUrl
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

import java.time.Year
import java.util.Locale

class BookInfoXmlConverterTest extends AnyFlatSpec with should.Matchers with BookInfoXmlConverter {

  nameOf(bookInfoXmlDecoder) should "parse book info from XML content" in {
    val xml =
      // @formatter:off
      <book version="2.01" name="Book Name" language="ru">
        <displayName>Book Display Name</displayName>
        <displayShortName>PV-2001</displayShortName>
        <edition>PV2001</edition>
        <releaseDate>1999</releaseDate>
        <description>Some Description</description>
        <preface>
          Preface Text
        </preface>
        <creators>
          <creator>Creator 1</creator>
          <creator>Creator 2</creator>
        </creators>
        <editors>
          <editor>Editor 1</editor>
          <editor>Editor 2</editor>
        </editors>
        <psalms>
          <ref>PV2001-psalms/ref1.pslm</ref>
          <ref>PV2001-psalms/ref2.pslm</ref>
        </psalms>
      </book>
      // @formatter:on

    val Valid(bookInfo) = xml.decode[BookInfo]
    bookInfo.version shouldBe Version("2.01")
    bookInfo.name shouldBe "Book Name"
    bookInfo.language shouldBe Locale.of("ru")
    bookInfo.displayName shouldBe "Book Display Name"
    bookInfo.displayShortName shouldBe "PV-2001"
    bookInfo.edition shouldBe "PV2001"
    bookInfo.releaseDate shouldBe Some(Year.parse("1999"))
    bookInfo.description shouldBe Some("Some Description")
    bookInfo.preface shouldBe Some("Preface Text")
    bookInfo.creators shouldBe List("Creator 1", "Creator 2")
    bookInfo.editors shouldBe List("Editor 1", "Editor 2")
    bookInfo.psalmRefs shouldBe List(RelativeUrl.parse("PV2001-psalms/ref1.pslm"), RelativeUrl.parse("PV2001-psalms/ref2.pslm"))
  }

  nameOf(bookInfoXmlEncoder) should "serialize book info to XML" in {
    val actual =
      BookInfo(
        version = Version("2.01"),
        name = "Book Name",
        language = Locale.of("ru"),
        displayName = "Book Display Name",
        displayShortName = "PV-2001",
        edition = "PV2001",
        releaseDate = Some(Year.parse("1999")),
        description = Some("Some Description"),
        preface = Some("Preface Text"),
        creators = List("Creator 1", "Creator 2"),
        editors = List("Editor 1", "Editor 2"),
        psalmRefs = List(RelativeUrl.parse("PV2001-psalms/ref1.pslm"), RelativeUrl.parse("PV2001-psalms/ref2.pslm"))
      ).encode

    val expected =
      // @formatter:off
      <book version="2.1" name="Book Name" language="ru">
        <displayName>Book Display Name</displayName>
        <displayShortName>PV-2001</displayShortName>
        <edition>PV2001</edition>
        <releaseDate>1999</releaseDate>
        <description>Some Description</description>
        <preface>
          Preface Text
        </preface>
        <creators>
          <creator>Creator 1</creator>
          <creator>Creator 2</creator>
        </creators>
        <editors>
          <editor>Editor 1</editor>
          <editor>Editor 2</editor>
        </editors>
        <psalms>
          <ref>PV2001-psalms/ref1.pslm</ref>
          <ref>PV2001-psalms/ref2.pslm</ref>
        </psalms>
      </book>
    // @formatter:on
    actual.normalize.toString() shouldBe expected.normalize.toString()
  }

  classOf[BookInfoXmlConverter].getSimpleName should "convert book info to XML and parse it back" in {
    val expected =
      BookInfo(
        version = Version("2.01"),
        name = "Book Name",
        language = Locale.of("ru"),
        displayName = "Book Display Name",
        displayShortName = "PV-2001",
        edition = "PV2001",
        releaseDate = Some(Year.parse("1999")),
        description = Some("Some Description"),
        preface = Some("Preface Text"),
        creators = List("Creator 1", "Creator 2"),
        editors = List("Editor 1", "Editor 2"),
        psalmRefs = List(RelativeUrl.parse("PV2001-psalms/ref1.pslm"), RelativeUrl.parse("PV2001-psalms/ref2.pslm"))
      )
    val Valid(actual) = expected.encode.normalize.decode[BookInfo]
    actual shouldBe expected
  }
}
