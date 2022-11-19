package com.alelk.pws.library_manager
package xml_support

import model.{PsalmChorus, PsalmPart, PsalmVerse}

import advxml.data.*
import advxml.implicits.*
import advxml.transform.XmlZoom.*
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.all.*

trait PsalmPartXmlConverter {

  implicit lazy val psalmPartDecoder: XmlDecoder[PsalmPart] = XmlDecoder.of { psalmPart =>
    lazy val data = (
      $(psalmPart).attr("number").asValidated[String].map(_.split(",").map(_.trim.toInt).toSet),
      psalmPart.text.asValidated[String].map(_.split("\n").map(_.trim).filter(!_.isBlank).mkString("\n"))
    )
    psalmPart.label.asValidated[String] match
      case Valid("verse") => data.mapN(PsalmVerse.apply)
      case Valid("chorus") => data.mapN(PsalmChorus.apply)
      case l: Invalid[?] => l
      case Valid(l) => IllegalArgumentException(s"Unexpected psalm part type: $l").invalidNel
  }

  implicit lazy val psalmVerseEncoder: XmlEncoder[PsalmVerse] = XmlEncoder.of { psalmPart =>
    // @formatter:off
    <verse number={psalmPart.numbers.toList.sorted.mkString(",")}>
      {psalmPart.text}
    </verse>
    // @formatter:on
  }

  implicit lazy val psalmChorusEncoder: XmlEncoder[PsalmChorus] = XmlEncoder.of { psalmPart =>
    // @formatter:off
    <chorus number={psalmPart.numbers.toList.sorted.mkString(",")}>
      {psalmPart.text}
    </chorus>
    // @formatter:on
  }

  implicit lazy val psalmPartEncoder: XmlEncoder[PsalmPart] = XmlEncoder.of {
    case p: PsalmVerse => p.encode
    case p: PsalmChorus => p.encode
  }

}
