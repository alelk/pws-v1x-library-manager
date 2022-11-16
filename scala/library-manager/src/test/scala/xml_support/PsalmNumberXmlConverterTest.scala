package com.alelk.pws.library_manager
package xml_support

import model.{Psalm, PsalmNumber}

import advxml.data.ValidatedNelThrow
import advxml.implicits.*
import cats.data.Validated.Valid
import com.github.dwickern.macros.NameOf.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

class PsalmNumberXmlConverterTest extends AnyFlatSpec with should.Matchers with PsalmNumberXmlConverter {

  nameOf(psalmNumberDecoder) should "parse psalm number from XML content" in {
    val Valid(psalmNumber) = <number edition="PV3300">123</number>.decode[PsalmNumber]
    psalmNumber.number shouldBe 123
    psalmNumber.bookEdition shouldBe "PV3300"
  }

  nameOf(psalmNumberEncoder) should "serialize psalm number to XML" in {
    PsalmNumber("PV3300", 123).encode.normalize.toString() shouldBe <number edition="PV3300">123</number>.normalize.toString()
  }

  classOf[PsalmNumberXmlConverter].getSimpleName should "serialize psalm number to XML and deserialize it back" in {
    val expected = PsalmNumber("PV2000", 222)
    val Valid(actual) = expected.encode.decode[PsalmNumber]
    actual shouldBe expected
  }
}
