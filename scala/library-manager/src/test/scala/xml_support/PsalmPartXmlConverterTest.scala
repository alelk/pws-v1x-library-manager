package com.alelk.pws.library_manager
package xml_support

import model.{Psalm, PsalmChorus, PsalmNumber, PsalmPart, PsalmVerse}

import advxml.data.ValidatedNelThrow
import advxml.implicits.*
import cats.data.Validated.Valid
import com.github.dwickern.macros.NameOf.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

class PsalmPartXmlConverterTest extends AnyFlatSpec with should.Matchers with PsalmPartXmlConverter {

  nameOf(psalmPartDecoder) should "parse psalm verse from XML content" in {
    val Valid(psalmPart) =
      <verse numbers="1,2,3">
        Line 1
        Line 2
      </verse>.decode[PsalmPart]

    psalmPart shouldBe a[PsalmVerse]
    psalmPart.numbers shouldBe Set(1, 2, 3)
    psalmPart.text shouldBe "Line 1\nLine 2"
  }

  it should "parse psalm chorus from XML content" in {
    val Valid(psalmPart) =
      <chorus numbers="1,4,7">
        Line 1
        Line 2
      </chorus>.decode[PsalmPart]

    psalmPart shouldBe a[PsalmChorus]
    psalmPart.numbers shouldBe Set(1, 4, 7)
    psalmPart.text shouldBe "Line 1\nLine 2"
  }

  nameOf(psalmPartEncoder) should "serialize psalm verse to XML" in {
    val actual = (PsalmVerse(numbers = Set(1, 2, 3), text = "Line 1\nLine 2"): PsalmPart).encode.normalize.toString()
    val expected =
      <verse numbers="1,2,3">
        {"Line 1\nLine 2"}
      </verse>
    actual shouldBe expected.normalize.toString()
  }

  it should "serialize psalm chorus to XML" in {
    val actual = (PsalmChorus(numbers = Set(1, 3, 5), text = "Line 1\nLine 2"): PsalmPart).encode.normalize.toString()
    val expected =
      <chorus numbers="1,3,5">
        {"Line 1\nLine 2"}
      </chorus>
    actual shouldBe expected.normalize.toString()
  }

  classOf[PsalmPartXmlConverter].getSimpleName should "serialize psalm part to XML and deserialize it back" in {
    val expected1 = PsalmVerse(numbers = Set(1, 2, 3), text = "Line 1\nLine 2")
    val expected2 = PsalmChorus(numbers = Set(1, 3, 5), text = "Line 1\nLine 2")
    val Valid(actual1) = expected1.encode.decode[PsalmPart]
    val Valid(actual2) = expected2.encode.decode[PsalmPart]
    actual1 shouldBe expected1
    actual2 shouldBe expected2
  }
}
