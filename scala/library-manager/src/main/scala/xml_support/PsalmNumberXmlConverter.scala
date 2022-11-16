package com.alelk.pws.library_manager
package xml_support

import model.PsalmNumber

import advxml.data.{XmlDecoder, XmlEncoder}
import advxml.transform.XmlZoom._
import advxml.data.*
import advxml.implicits.*
import cats.syntax.all.*

trait PsalmNumberXmlConverter {
  implicit lazy val psalmNumberDecoder: XmlDecoder[PsalmNumber] = XmlDecoder.of { psalmNumber =>
    (
      psalmNumber.attr("edition").asValidated[String],
      psalmNumber.text.trim.asValidated[Int]
      ).mapN(PsalmNumber.apply)
  }

  implicit lazy val psalmNumberEncoder: XmlEncoder[PsalmNumber] = XmlEncoder.of { psalmNumber =>
    // @formatter:off
    <number edition={psalmNumber.bookEdition}>{psalmNumber.number}</number>
    // @formatter:on
  }
}
