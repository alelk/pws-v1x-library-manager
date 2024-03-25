package com.alelk.pws.library_manager
package v2x.yaml_support

import model.*
import v2x.model.{BookV2, PsalmV2}

import com.github.dwickern.macros.NameOf.nameOf
import io.circe.syntax.*
import io.circe.yaml.*
import io.circe.yaml.Printer.StringStyle
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.time.Year
import java.util.Locale

class BookV2YamlConverterTest extends AnyFlatSpec with should.Matchers with PsalmV2YamlConverter with BookV2YamlConverter {

  private val printer = Printer(stringStyle = StringStyle.Plain, preserveOrder = true)

  nameOf(bookV2YamlEncoder) should "convert BookV2 to YAML" in {
    val book = BookV2(
      version = Version("2.1"), name = "BookName", locale = Locale.of("en"), displayName = "Book Display Name",
      displayShortName = "Book Short Name", signature = "book1",
      releaseDate = Some(Year.parse("2024")), description = Some("Multiline\nBook\nDescription"),
      preface = Some("Preface"), creators = List("Author1", "Author2"), editors = List("Editor1", "Editor2"),
      psalms = List(
        PsalmV2(
          id = 1,
          version = Version("1.1"),
          name = "Psalm Name",
          numbers = List(PsalmNumber("book1", 10), PsalmNumber("book2", 2)),
          text = List(
            PsalmVerse(Set(1), "Verse 1 Line 1\nVerse 1 Line 2"),
            PsalmChorus(Set(2, 4), "Chorus 1 Line 1\nChorus 1 Line 2"),
            PsalmVerse(Set(3, 5), "Verse 2 Line 1\nVerse 2 Line 2"),
            PsalmChorus(Set(6), "Chorus 2 Line 1\nChorus 2 Line 2")),
          locale = Locale.of("en"),
          references = List(BibleRef("Bible ref 1"), BibleRef("Bible ref 2")),
          tonalities = List(Tonality("tonality 1"), Tonality("tonality 2")),
          author = Some("Author"),
          translator = Some("Translator"),
          composer = Some("Composer")),
        PsalmV2(
          id = 7,
          version = Version("1.1"),
          name = "Psalm Name",
          numbers = List(PsalmNumber("book1", 1), PsalmNumber("book2", 100)),
          text = List(
            PsalmVerse(Set(1), "Verse 1 Line 1\nVerse 1 Line 2"),
            PsalmChorus(Set(2, 4), "Chorus 2,4 Line 1\nChorus 2,4 Line 2"),
            PsalmVerse(Set(3), "Verse 2 Line 1\nVerse 2 Line 2"),
            PsalmVerse(Set(5), "Verse 3 Line 1\nVerse 3 Line 2")),
          locale = Locale.of("en"))
      )
    )

    val yaml = printer.pretty(book.asJson)
    println(yaml)

    val expectedYaml =
      """
        |name: BookName
        |displayName: Book Display Name
        |displayShortName: Book Short Name
        |version: '2.1'
        |locale: en
        |signature: book1
        |releaseDate: '2024'
        |description: |-
        |  Multiline
        |  Book
        |  Description
        |creators:
        |- Author1
        |- Author2
        |editors:
        |- Editor1
        |- Editor2
        |preface: Preface
        |psalms:
        |- id: 1
        |  name: Psalm Name
        |  numbers:
        |    book1: 10
        |    book2: 2
        |  version: '1.1'
        |  text: |-
        |    Verse 1.
        |    Verse 1 Line 1
        |    Verse 1 Line 2
        |
        |    Chorus 1.
        |    Chorus 1 Line 1
        |    Chorus 1 Line 2
        |
        |    Verse 2.
        |    Verse 2 Line 1
        |    Verse 2 Line 2
        |
        |    [Chorus 1]
        |
        |    [Verse 2]
        |
        |    Chorus 2.
        |    Chorus 2 Line 1
        |    Chorus 2 Line 2
        |  locale: en
        |  tonalities:
        |  - tonality 1
        |  - tonality 2
        |  references:
        |  - type: bibleRef
        |    value: Bible ref 1
        |  - type: bibleRef
        |    value: Bible ref 2
        |  author: Author
        |  composer: Composer
        |  translator: Translator
        |- id: 7
        |  name: Psalm Name
        |  numbers:
        |    book1: 1
        |    book2: 100
        |  version: '1.1'
        |  text: |-
        |    1.
        |    Verse 1 Line 1
        |    Verse 1 Line 2
        |
        |    Chorus.
        |    Chorus 2,4 Line 1
        |    Chorus 2,4 Line 2
        |
        |    2.
        |    Verse 2 Line 1
        |    Verse 2 Line 2
        |
        |    [Chorus]
        |
        |    3.
        |    Verse 3 Line 1
        |    Verse 3 Line 2
        |  locale: en
        |""".stripMargin
    parser.parse(yaml) shouldBe parser.parse(expectedYaml)
  }

}
