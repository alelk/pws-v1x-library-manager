package com.alelk.pws.library_manager
package v2x.yaml_support

import model.*
import v2x.model.PsalmV2

import com.github.dwickern.macros.NameOf.nameOf
import io.circe.syntax.*
import io.circe.yaml.*
import io.circe.yaml.Printer.StringStyle
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.util.Locale

class PsalmV2YamlConverterTest extends AnyFlatSpec with should.Matchers with PsalmV2YamlConverter {

  nameOf(psalmV2YamlEncoder) should "convert Psalm to YAML when psalm verse repeated and multiple choruses" in {
    val p =
      PsalmV2(
        id = 1,
        version = Version("1.1"),
        name = "Psalm Name",
        numbers = List(PsalmNumber("book1", 1), PsalmNumber("book2", 2)),
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
        composer = Some("Composer"))

    val printer = Printer(stringStyle = StringStyle.Plain, preserveOrder = true)

    val yaml = printer.pretty(p.asJson)

    println(yaml)

    val expectedYaml =
      s"""
         |id: 1
         |translator: Translator
         |numbers:
         |  book1: 1
         |  book2: 2
         |author: Author
         |name: Psalm Name
         |version: '1.1'
         |text: |-
         |  Verse 1.
         |  Verse 1 Line 1
         |  Verse 1 Line 2
         |
         |  Chorus 1.
         |  Chorus 1 Line 1
         |  Chorus 1 Line 2
         |
         |  Verse 2.
         |  Verse 2 Line 1
         |  Verse 2 Line 2
         |
         |  [Chorus 1]
         |
         |  [Verse 2]
         |
         |  Chorus 2.
         |  Chorus 2 Line 1
         |  Chorus 2 Line 2
         |locale: en
         |composer: Composer
         |tonalities:
         |- tonality 1
         |- tonality 2
         |references:
         |- type: bibleRef
         |  value: Bible ref 1
         |- type: bibleRef
         |  value: Bible ref 2
         """.stripMargin

    val parsed1 = parser.parse(expectedYaml)
    val parsed2 = parser.parse(yaml)
    parsed1.isRight shouldBe true
    parsed1 shouldBe parsed2
  }

  nameOf(psalmV2YamlEncoder) should "convert Psalm to YAML when psalm verses are not repeated" in {
    val p =
      PsalmV2(
        id = 2,
        version = Version("1.1"),
        name = "Psalm Name",
        numbers = List(PsalmNumber("book1", 1), PsalmNumber("book2", 2)),
        text = List(
          PsalmVerse(Set(1), "Verse 1 Line 1\nVerse 1 Line 2"),
          PsalmChorus(Set(2, 4), "Chorus 2,4 Line 1\nChorus 2,4 Line 2"),
          PsalmVerse(Set(3), "Verse 2 Line 1\nVerse 2 Line 2"),
          PsalmVerse(Set(5), "Verse 3 Line 1\nVerse 3 Line 2")),
        locale = Locale.of("en"))

    val printer = Printer(stringStyle = StringStyle.Plain, preserveOrder = true)

    val yaml = printer.pretty(p.asJson)

    println(yaml)

    val expectedYaml =
      s"""
         |id: 2
         |name: Psalm Name
         |numbers:
         |  book1: 1
         |  book2: 2
         |version: '1.1'
         |text: |-
         |  1.
         |  Verse 1 Line 1
         |  Verse 1 Line 2
         |
         |  Chorus.
         |  Chorus 2,4 Line 1
         |  Chorus 2,4 Line 2
         |
         |  2.
         |  Verse 2 Line 1
         |  Verse 2 Line 2
         |
         |  [Chorus]
         |
         |  3.
         |  Verse 3 Line 1
         |  Verse 3 Line 2
         |locale: en
         |""".stripMargin
    val parsed1 = parser.parse(expectedYaml)
    val parsed2 = parser.parse(yaml)
    parsed1.isRight shouldBe true
    parsed1 shouldBe parsed2
  }

}
