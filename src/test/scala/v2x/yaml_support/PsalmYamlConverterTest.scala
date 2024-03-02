package com.alelk.pws.library_manager
package v2x.yaml_support

import model.*

import com.github.dwickern.macros.NameOf.nameOf
import io.circe.syntax.*
import io.circe.yaml.*
import io.circe.yaml.Printer.StringStyle
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class PsalmYamlConverterTest extends AnyFlatSpec with should.Matchers with PsalmYamlConverter {

  nameOf(psalmYamlEncoder) should "convert Psalm to YAML" in {
    val p: Psalm =
      Psalm(
        version = "1",
        name = "Psalm Name",
        numbers = List(PsalmNumber("book1", 1), PsalmNumber("book2", 2)),
        text = List(
          PsalmVerse(Set(1), "Verse 1 Line 1\nVerse 1 Line 2"),
          PsalmChorus(Set(2, 4), "Chorus 2,4 Line 1\nChorus 2,4 Line 2"),
          PsalmVerse(Set(3, 5), "Verse 3,5 Line 1\nVerse 3,5 Line 2")),
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
         |translator: Translator
         |numbers:
         |  book1: 1
         |  book2: 2
         |author: Author
         |name: Psalm Name
         |version: '1'
         |text: |-
         |  [Verse 1]
         |  Verse 1 Line 1
         |  Verse 1 Line 2
         |
         |  [Chorus 1]
         |  Chorus 2,4 Line 1
         |  Chorus 2,4 Line 2
         |
         |  [Verse 2]
         |  Verse 3,5 Line 1
         |  Verse 3,5 Line 2
         |
         |  [Chorus 1]
         |
         |  [Verse 2]
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

}
