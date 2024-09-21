package com.alelk.pws.library_manager
package xml_support

import model.*

import advxml.data.*
import advxml.implicits.*
import advxml.transform.XmlZoom.*
import cats.syntax.all.*

import scala.util.Try
import scala.xml.NodeSeq

trait PsalmXmlConverter {
  this: PsalmNumberXmlConverter with PsalmPartXmlConverter with ReferenceXmlConverter =>

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
      $(psalm).attr("version").asValidated[String].andThen(v => if (v.toLowerCase == "null") Version("0.1").valid else Try(Version.apply(v)).toValidated.toValidatedNel),
      $(psalm).attr("name").asValidated[String],
      $(psalm).numbers.number.run[ValidatedNelThrow].andThen { psalmNumbers =>
        psalmNumbers.map(_.asValidated[PsalmNumber]).toList.sequence
      },
      $(psalm).text.down("_").run[ValidatedNelThrow].andThen { psalmParts =>
        psalmParts.map(_.asValidated[PsalmPart]).toList.sequence
      },
      $(psalm).references.run[Option] match {
        case Some(node) => $(node).down("_").run[ValidatedNelThrow].andThen { references =>
          references.map(_.asValidated[Reference]).toList.sequence
        }
        case None => Nil.valid
      },
      $(psalm).annotation.content.as[Option[String]].valid,
      $(psalm).tonalities.run[Option] match {
        case Some(node) => $(node).tonality.run[ValidatedNelThrow].andThen { tonalities =>
          tonalities.map(_.asValidated[Tonality]).toList.sequence
        }
        case None => Nil.valid
      },
      $(psalm).author.content.as[Option[String]].valid,
      $(psalm).translator.content.as[Option[String]].valid,
      $(psalm).composer.content.as[Option[String]].valid
    ).mapN { (version, name, numbers, text, references, annotation, tonalities, author, translator, composer) =>
      val refs = references :++ annotation.map(BibleRef.apply).toList
      Psalm(version, name, numbers, text, refs, tonalities, author, translator, composer)
    }
  }

  implicit lazy val psalmEncoder: XmlEncoder[Psalm] = XmlEncoder.of { psalm =>
    // @formatter:off
    <psalm version={psalm.version.toString} name={psalm.name}>
      <numbers>
        {psalm.numbers.map(_.encode)}
      </numbers>
      <tonalities>
        {psalm.tonalities.map(_.encode)}
      </tonalities>
      {psalm.author.map(v => <author>{v}</author> ).getOrElse(NodeSeq.Empty)}
      {psalm.translator.map(v => <translator>{v}</translator>).getOrElse(NodeSeq.Empty)}
      {psalm.composer.map(v => <composer>{v}</composer>).getOrElse(NodeSeq.Empty)}
      {psalm.references match {
        case Nil => NodeSeq.Empty
        case references =>
          <references>
            {references.map(_.encode)}
          </references>
      }}
      <text>
        {psalm.text.map(_.encode)}
      </text>
    </psalm>
    // @formatter:on
  }

}
