package com.alelk.pws.library_manager
package xml_support

import model.*

import advxml.data.ValidatedNelThrow
import advxml.implicits.*
import cats.data.Validated.Valid
import com.github.dwickern.macros.NameOf.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

class ReferenceXmlConverterTest extends AnyFlatSpec with should.Matchers with ReferenceXmlConverter {

  nameOf(referenceXmlDecoder) should "parse psalm reference from XML content" in {
    val Valid(ref) =
        <psalmref reason="variation" volume="88" edition="PV3000" number="123"/>.decode[Reference]

    ref shouldBe a[PsalmRef]
    val psalmRef = ref.asInstanceOf[PsalmRef]
    psalmRef.reason shouldBe PsalmRefReason.Variation
    psalmRef.volume shouldBe 88
    psalmRef.number.bookEdition shouldBe "PV3000"
    psalmRef.number.number shouldBe 123
  }

  it should "parse bible reference from XML content" in {
    val Valid(ref) =
      <bibleref>Some Bible text</bibleref>.decode[Reference]

    ref shouldBe a[BibleRef]
    val bibleRef = ref.asInstanceOf[BibleRef]
    bibleRef.text shouldBe "Some Bible text"
  }

  nameOf(referenceEncoder) should "serialize psalm reference to XML" in {
    val actual = (PsalmRef(PsalmRefReason.Variation, 89, PsalmNumber("PV3300", 100)): Reference).encode.toString()
    val expected = <psalmref reason="variation" volume="89" edition="PV3300" number="100"/>
    actual shouldBe expected.toString()
  }

  it should "serialize bible reference to XML" in {
    val actual = (BibleRef("Some Bible text"): Reference).encode.toString()
    val expected = <bibleref>Some Bible text</bibleref>
    actual shouldBe expected.toString()
  }

  classOf[ReferenceXmlConverter].getSimpleName can "serialize psalm reference to XML and deserialize it back" in {
    val expected: Reference = PsalmRef(PsalmRefReason.Variation, 89, PsalmNumber("PV3300", 100))
    val Valid(actual) = expected.encode.decode[Reference]
    actual shouldBe expected
  }

  it can "serialize bible reference to XML and deserialize it back" in {
    val expected: Reference = BibleRef("Some Bible text")
    val Valid(actual) = expected.encode.decode[Reference]
    actual shouldBe expected
  }
}
