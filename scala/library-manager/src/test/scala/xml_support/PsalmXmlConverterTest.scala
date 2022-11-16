package com.alelk.pws.library_manager
package xml_support

import model.{Psalm, PsalmNumber, Tonality}

import advxml.data.ValidatedNelThrow
import advxml.implicits.*
import cats.data.Validated.Valid
import com.github.dwickern.macros.NameOf.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

class PsalmXmlConverterTest extends AnyFlatSpec with should.Matchers with PsalmXmlConverter with PsalmNumberXmlConverter {

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
        composer = Some("Composer")
      ).encode

    println(actual)

    val expected =
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
      </psalm>
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
        composer = Some("Composer"))
    val Valid(actual) = expected.encode.normalize.decode[Psalm]
    actual shouldBe expected
  }
}
