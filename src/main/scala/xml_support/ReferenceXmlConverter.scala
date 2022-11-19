package com.alelk.pws.library_manager
package xml_support

import model.{BibleRef, PsalmNumber, PsalmRef, PsalmRefReason, Reference}

import advxml.data.*
import advxml.implicits.*
import advxml.transform.XmlZoom.*
import cats.syntax.all.*

trait ReferenceXmlConverter {

  implicit lazy val psalmRefXmlDecoder: XmlDecoder[PsalmRef] = XmlDecoder.of { psalmRef =>
    (
      psalmRef.attr("reason").asValidated[String].andThen {
        PsalmRefReason.from
      },
      psalmRef.attr("volume").asValidated[Int],
      psalmRef.attr("edition").asValidated[String].andThen { edition =>
        psalmRef.attr("number").asValidated[Int].map(number => PsalmNumber(edition, number))
      }
      ).mapN(PsalmRef.apply)
  }

  implicit lazy val bibleRefXmlDecoder: XmlDecoder[BibleRef] = XmlDecoder.of { bibleRef =>
    bibleRef.text.asValidated[String].map(text => BibleRef(text = text.trim))
  }

  implicit lazy val referenceXmlDecoder: XmlDecoder[Reference] = XmlDecoder.of { ref =>
    ref.label.asValidated[String].andThen {
      case "bibleref" => ref.decode[BibleRef]
      case "psalmref" => ref.decode[PsalmRef]
      case refType => IllegalArgumentException(s"Unexpected reference type: $refType").invalidNel
    }
  }

  implicit lazy val psalmRefXmlEncoder: XmlEncoder[PsalmRef] = XmlEncoder.of { psalmRef =>
    // @formatter:off
    <psalmref reason={psalmRef.reason.signature} volume={psalmRef.volume.toString} edition={psalmRef.number.bookEdition} number={psalmRef.number.number.toString}/>
    // @formatter:on
  }

  implicit lazy val bibleRefXmlEncoder: XmlEncoder[BibleRef] = XmlEncoder.of { bibleRef =>
    // @formatter:off
    <bibleref>{bibleRef.text}</bibleref>
    // @formatter:on
  }

  implicit lazy val referenceEncoder: XmlEncoder[Reference] = XmlEncoder.of {
    case r: BibleRef => r.encode
    case r: PsalmRef => r.encode
  }

}
