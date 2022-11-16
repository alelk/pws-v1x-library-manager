package com.alelk.pws.library_manager
package xml_support

import model.{Psalm, PsalmNumber, Tonality}

import advxml.transform.XmlZoom.*
import advxml.data.*
import advxml.implicits.*
import cats.syntax.all.*

import scala.xml.{NodeSeq, Utility}

trait PsalmXmlConverter {
  this: PsalmNumberXmlConverter =>

  implicit lazy val tonalityDecoder: XmlDecoder[Tonality] = XmlDecoder.of { tonality =>
    tonality.text.asValidated[String].map(v => Tonality(v.trim))
  }

  implicit lazy val tonalityEncoder: XmlEncoder[Tonality] = XmlEncoder.of { tonality =>
    // @formatter:off
    <tonality>{tonality.value}</tonality>
    // @formatter:on
  }

  implicit lazy val psalmDecoder: XmlDecoder[Psalm] = XmlDecoder.of { psalm =>
    (
      $(psalm).attr("version").asValidated[String],
      $(psalm).attr("name").asValidated[String],
      $(psalm).numbers.number.run[ValidatedNelThrow].andThen { psalmNumbers =>
        psalmNumbers.map(_.asValidated[PsalmNumber]).toList.sequence
      },
      $(psalm).tonalities.tonality.run[ValidatedNelThrow].andThen { tonalities =>
        tonalities.map(_.asValidated[Tonality]).toList.sequence
      },
      $(psalm).author.content.as[Option[String]].valid,
      $(psalm).translator.content.as[Option[String]].valid,
      $(psalm).composer.content.as[Option[String]].valid
      ).mapN(Psalm.apply)
  }

  implicit lazy val psalmEncoder: XmlEncoder[Psalm] = XmlEncoder.of { psalm =>
    // @formatter:off
    <psalm version={psalm.version} name={psalm.name}>
      <numbers>
        {psalm.numbers.map(_.encode)}
      </numbers>
      <tonalities>
        {psalm.tonalities.map(_.encode)}
      </tonalities>
      {psalm.author.map(v => <author>{v}</author> ).getOrElse(NodeSeq.Empty)}
      {psalm.translator.map(v => <translator>{v}</translator>).getOrElse(NodeSeq.Empty)}
      {psalm.composer.map(v => <composer>{v}</composer>).getOrElse(NodeSeq.Empty)}
    </psalm>
    // @formatter:on
  }

}
