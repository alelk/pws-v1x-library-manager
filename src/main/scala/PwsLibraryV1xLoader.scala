package com.alelk.pws.library_manager

import model.{BookInfo, LibraryInfo, Psalm}
import xml_support.*

import advxml.data.*
import advxml.implicits.*
import cats.syntax.all.*
import io.lemonlabs.uri.{RelativeUrl, Url}

import java.net.URI
import scala.xml.XML

class Library private[library_manager](val info: LibraryInfo, val url: Url) extends BookInfoXmlConverter {
  lazy val books: ValidatedNelThrow[List[Book]] =
    info.bookRefs.map { ref =>
      val bookUrl = Url.parse(URI.create(url.toString).resolve(ref.reference.toString).toString)
      XML.load(bookUrl.toString).decode[BookInfo]
        .map(Book(_, bookUrl))
    }.sequence
}

case class PsalmInfo(relativeUrl: RelativeUrl, resolvedUrl: Url)

class Book private[library_manager](val info: BookInfo, val url: Url)
  extends PsalmXmlConverter with PsalmPartXmlConverter with PsalmNumberXmlConverter with ReferenceXmlConverter {

  lazy val psalms: ValidatedNelThrow[List[(Psalm, PsalmInfo)]] =
    info.psalmRefs.map { ref =>
      val psalmUrl = Url.parse(URI.create(url.toString).resolve(ref.toString).toString)
      XML
        .load(psalmUrl.toString)
        .decode[Psalm]
        .leftMap(_.map(e => UnsupportedOperationException(s"unable to parse psalm $ref: ${e.getMessage}")))
        .map(_ -> PsalmInfo(ref, psalmUrl))
    }.sequence
}

trait PwsLibraryV1xLoader extends LibraryInfoXmlConverter {

  def withLibrary[T](url: Url)(f: Library => ValidatedNelThrow[T]): ValidatedNelThrow[T] =
    XML.load(url.toString)
      .decode[LibraryInfo]
      .andThen { libInfo =>
        f(Library(libInfo, url))
      }
}