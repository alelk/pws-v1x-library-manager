package com.alelk.pws.library_manager
package xml_support

import model.{Psalm, PsalmNumber}

import advxml.transform.XmlZoom._
import advxml.data.*
import advxml.implicits.*
import cats.syntax.all.*

trait PsalmXmlConverter {
  this: PsalmNumberXmlConverter =>

  implicit lazy val psalmDecoder: XmlDecoder[Psalm] = XmlDecoder.of { psalm =>
    (
      $(psalm).attr("version").asValidated[String],
      $(psalm).attr("name").asValidated[String],
      $(psalm).numbers.number.run[ValidatedNelThrow].andThen { psalmNumbers =>
        psalmNumbers.map(_.asValidated[PsalmNumber]).toList.sequence
      }
      ).mapN(Psalm.apply)
  }

  implicit lazy val psalmEncoder: XmlEncoder[Psalm] = XmlEncoder.of { psalm =>
    <psalm version={psalm.version} name={psalm.name}>
      <numbers>
        {psalm.numbers.map(_.encode)}
      </numbers>
    </psalm>
  }

}
