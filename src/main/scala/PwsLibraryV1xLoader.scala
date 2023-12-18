package com.alelk.pws.library_manager

import model.{BookInfo, LibraryInfo, Psalm}
import xml_support.{PsalmNumberXmlConverter, PsalmPartXmlConverter, PsalmXmlConverter, ReferenceXmlConverter, *}

import advxml.data.*
import advxml.implicits.*
import advxml.transform.XmlZoom.*
import cats.syntax.all.*
import io.lemonlabs.uri.Url

import java.net.{URI, URL}
import scala.xml.XML

class Library private[library_manager](val info: LibraryInfo, val url: Url) extends BookInfoXmlConverter {
  lazy val books: ValidatedNelThrow[List[Book]] =
    info.bookRefs.map { ref =>
      val bookUrl = Url.parse(URI.create(url.toString).resolve(ref.reference.toString).toString)
      XML.load(bookUrl.toString).decode[BookInfo]
        .map(Book(_, bookUrl))
    }.sequence
}

class Book private[library_manager](val info: BookInfo, val url: Url)
  extends PsalmXmlConverter with PsalmPartXmlConverter with PsalmNumberXmlConverter with ReferenceXmlConverter {

  lazy val psalms: ValidatedNelThrow[List[Psalm]] =
    info.psalmRefs.map { ref =>
      val psalmUrl = Url.parse(URI.create(url.toString).resolve(ref.toString).toString)
      XML.load(psalmUrl.toString).decode[Psalm]
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

object PwsLibraryHelper extends PwsLibraryV1xLoader {
  def parseUrl(url: String): Url = Url.parse(url)
}