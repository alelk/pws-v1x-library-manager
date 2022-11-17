package com.alelk.pws.library_manager
package xml_support

import model.BookInfo

import advxml.data.*
import advxml.implicits.*
import advxml.transform.XmlZoom.*
import cats.syntax.all.*
import io.lemonlabs.uri.RelativeUrl

import java.util.Locale
import scala.util.Try
import scala.xml.NodeSeq

trait BookInfoXmlConverter {

  implicit lazy val bookInfoXmlDecoder: XmlDecoder[BookInfo] = XmlDecoder.of { book =>
    (
      $(book).attr("version").asValidated[String],
      $(book).attr("name").asValidated[String],
      $(book).attr("language").asValidated[String].andThen { l =>
        ValidatedNelThrow.fromTry {
          Try {
            Locale.of(l)
          }
        }
      },
      $(book).displayName.content.asValidated[String].map(_.trim),
      $(book).displayShortName.content.asValidated[String].map(_.trim),
      $(book).edition.content.asValidated[String].map(_.trim),
      $(book).psalms.ref.run[ValidatedNelThrow].andThen { psalmRefs =>
        psalmRefs.map(ref => $(ref).content.asValidated[String].andThen { ref =>
          ValidatedNelThrow.fromTry(RelativeUrl.parseTry(ref.trim))
        }).toList.sequence
      },
      $(book).releaseDate.content.as[Option[String]].map(_.trim).valid,
      $(book).description.content.as[Option[String]].map(_.trim).valid,
      $(book).preface.content.as[Option[String]].map(_.trim).valid,
      $(book).creators.run[Option] match {
        case Some(node) => $(node).creator.run[ValidatedNelThrow].andThen { creators =>
          creators.map(v => $(v).content.asValidated[String].map(_.trim)).toList.sequence
        }
        case None => Nil.valid
      },
      $(book).editors.run[Option] match {
        case Some(node) => $(node).editor.run[ValidatedNelThrow].andThen { editors =>
          editors.map(v => $(v).content.asValidated[String].map(_.trim)).toList.sequence
        }
        case None => Nil.valid
      }
      ).mapN(BookInfo.apply)
  }

  implicit lazy val bookInfoXmlEncoder: XmlEncoder[BookInfo] = XmlEncoder.of { book =>
    // @formatter:off
    <book version={book.version} name={book.name} language={book.language.toString}>
      <displayName>{book.displayName}</displayName>
      <displayShortName>{book.displayShortName}</displayShortName>
      <edition>{book.edition}</edition>
      {book.releaseDate.map(v => <releaseDate>{v}</releaseDate>).getOrElse(NodeSeq.Empty)}
      {book.description.map(v => <description>{v}</description>).getOrElse(NodeSeq.Empty)}
      {book.preface.map(v => <preface>{v}</preface>).getOrElse(NodeSeq.Empty)}
      <creators>
        {book.creators.map(v => <creator>{v}</creator>)}
      </creators>
      <editors>
        {book.editors.map(v => <editor>{v}</editor>)}
      </editors>
      <psalms>
        {book.psalmRefs.map(r => <ref>{r.toString}</ref>)}
      </psalms>
    </book>
    // @formatter:on
  }

}
