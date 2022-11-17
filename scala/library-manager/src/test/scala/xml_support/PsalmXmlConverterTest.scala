package com.alelk.pws.library_manager
package xml_support

import model.{BibleRef, Psalm, PsalmChorus, PsalmNumber, PsalmRef, PsalmRefReason, PsalmVerse, Tonality}

import advxml.data.ValidatedNelThrow
import advxml.implicits.*
import cats.data.Validated.Valid
import com.github.dwickern.macros.NameOf.*
import org.scalactic.PrettyPair
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

import scala.xml.PrettyPrinter

class PsalmXmlConverterTest
  extends AnyFlatSpec
    with should.Matchers
    with PsalmXmlConverter with PsalmNumberXmlConverter with PsalmPartXmlConverter with ReferenceXmlConverter {

  nameOf(psalmDecoder) should "parse psalm from XML content" in {
    val xml =
      <psalm version="1.2.3" name="Psalm 1">
        <numbers>
          <number edition="PV3300">100</number>
          <number edition="PV1000">220</number>
        </numbers>
        <tonalities>
          <tonality>a major</tonality>
          <tonality>C major</tonality>
        </tonalities>
        <author>Author Name</author>
        <translator>Translator Name</translator>
        <composer>Composer Name</composer>
        <references>
          <bibleref>Some Bible text 1</bibleref>
          <psalmref reason="variation" volume="88" edition="PV100" number="10"/>
          <bibleref>Some Bible text 2</bibleref>
          <psalmref reason="variation" volume="70" edition="PV220" number="20"/>
        </references>
        <text>
          <verse numbers="1">
            Verse 1 Line 1
            Verse 1 Line 2
          </verse>
          <chorus numbers="2,4">
            Chorus Line 1
            Chorus Line 2
          </chorus>
          <verse numbers="3">
            Verse 2 Line 1
            Verse 2 Line 3
          </verse>
        </text>
      </psalm>

    val Valid(psalm) = xml.decode[Psalm]
    psalm.name shouldBe "Psalm 1"
    psalm.version shouldBe "1.2.3"
    psalm.numbers should have length 2
    val List(number1, number2) = psalm.numbers
    number1.bookEdition shouldBe "PV3300"
    number1.number shouldBe 100
    number2.bookEdition shouldBe "PV1000"
    number2.number shouldBe 220
    psalm.tonalities should have length 2
    val List(tonality1, tonality2) = psalm.tonalities
    tonality1.value shouldBe "a major"
    tonality2.value shouldBe "C major"
    psalm.author shouldBe Some("Author Name")
    psalm.translator shouldBe Some("Translator Name")
    psalm.composer shouldBe Some("Composer Name")
    psalm.references should matchPattern {
      case
        List(
        BibleRef("Some Bible text 1"),
        PsalmRef(PsalmRefReason.Variation, 88, PsalmNumber("PV100", 10)),
        BibleRef("Some Bible text 2"),
        PsalmRef(PsalmRefReason.Variation, 70, PsalmNumber("PV220", 20))) =>
    }
  }

  nameOf(psalmEncoder) should "serialize psalm to XML" in {
    val actual =
      Psalm(
        version = "1.2.0",
        name = "Psalm Name",
        numbers = List(PsalmNumber("PV3300", 123), PsalmNumber("PV2000", 10)),
        tonalities = List(Tonality("a-major"), Tonality("c-major")),
        author = Some("Author"),
        translator = Some("Translator"),
        composer = Some("Composer"),
        references = List(
          BibleRef("Some Bible text 1"),
          PsalmRef(PsalmRefReason.Variation, 88, PsalmNumber("PV3000", 10)),
          BibleRef("Some Bible text 2"),
          PsalmRef(PsalmRefReason.Variation, 70, PsalmNumber("PV2000", 20))
        ),
        text = List(
          PsalmVerse(numbers = Set(1), text = "Verse 1 Line 1\nVerse 1 Line 2"),
          PsalmChorus(numbers = Set(2, 4), text = "Chorus Line 1\nChorus Line 2"),
          PsalmVerse(numbers = Set(3), text = "Verse 2 Line 1\nVerse 2 Line 2"),
        )
      ).encode

    val expected =
    // @formatter:off
      <psalm version="1.2.0" name="Psalm Name">
        <numbers>
          <number edition="PV3300">123</number>
          <number edition="PV2000">10</number>
        </numbers>
        <tonalities>
          <tonality>a-major</tonality>
          <tonality>c-major</tonality>
        </tonalities>
        <author>Author</author>
        <translator>Translator</translator>
        <composer>Composer</composer>
        <references>
          <bibleref>Some Bible text 1</bibleref>
          <psalmref reason="variation" volume="88" edition="PV3000" number="10"/>
          <bibleref>Some Bible text 2</bibleref>
          <psalmref reason="variation" volume="70" edition="PV2000" number="20"/>
        </references>
        <text>
          <verse numbers="1">{"Verse 1 Line 1\nVerse 1 Line 2"}</verse>
          <chorus numbers="2,4">{"Chorus Line 1\nChorus Line 2"}</chorus>
          <verse numbers="3">{"Verse 2 Line 1\nVerse 2 Line 2"}</verse>
        </text>
      </psalm>
      // @formatter:on
    actual.normalize.toString() shouldBe expected.normalize.toString()
  }

  classOf[PsalmXmlConverter].getSimpleName should "convert Psalm to XML and parse it back" in {
    val expected =
      Psalm(
        version = "1.1.1",
        name = "Name",
        numbers = List(PsalmNumber("PV110", 1), PsalmNumber("PV2", 2)),
        tonalities = List(Tonality("A-major"), Tonality("c-minor")),
        author = Some("Author"),
        translator = Some("translator"),
        composer = Some("Composer"),
        references = List(
          BibleRef("Some Bible text 1"),
          PsalmRef(PsalmRefReason.Variation, 88, PsalmNumber("PV3000", 10)),
          BibleRef("Some Bible text 2"),
          PsalmRef(PsalmRefReason.Variation, 70, PsalmNumber("PV2000", 20))
        ),
        text = List(
          PsalmVerse(numbers = Set(1), text = "Verse 1 Line 1\nVerse 1 Line2"),
          PsalmChorus(numbers = Set(2, 4), text = "Chorus Line 1\nChorus Line2"),
          PsalmVerse(numbers = Set(3), text = "Verse 2 Line 1\nVerse 2 Line2"),
        ))
    val Valid(actual) = expected.encode.normalize.decode[Psalm]
    actual shouldBe expected
  }
}
