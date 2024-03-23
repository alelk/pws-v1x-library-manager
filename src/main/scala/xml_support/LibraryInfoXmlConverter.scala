package com.alelk.pws.library_manager
package xml_support

import model.{BookRef, LibraryInfo, Version}

import advxml.data.*
import advxml.implicits.*
import advxml.transform.XmlZoom.*
import cats.syntax.all.*
import io.lemonlabs.uri.RelativeUrl

trait LibraryInfoXmlConverter {

  implicit lazy val bookRefXmlDecoder: XmlDecoder[BookRef] = XmlDecoder.of { bookRef =>
    (
      $(bookRef).attr("preference").asValidated[Int],
      $(bookRef).content.asValidated[String].andThen { ref =>
        ValidatedNelThrow.fromTry(RelativeUrl.parseTry(ref.trim))
      }
      ).mapN(BookRef.apply)
  }

  implicit lazy val libraryInfoXmlDecoder: XmlDecoder[LibraryInfo] = XmlDecoder.of { libInfo =>
    (
      $(libInfo).attr("version").asValidated[String].map(Version.apply),
      $(libInfo).books.ref.run[ValidatedNelThrow].andThen { refs =>
        refs.map(_.decode[BookRef]).toList.sequence
      }
      ).mapN(LibraryInfo.apply)
  }

  implicit lazy val bookRefXmlEncoder: XmlEncoder[BookRef] = XmlEncoder.of { bookRef =>
    // @formatter:off
    <ref preference={bookRef.preference.toString}>{bookRef.reference.toString}</ref>
    // @formatter:on
  }

  implicit lazy val libraryInfoXmlEncoder: XmlEncoder[LibraryInfo] = XmlEncoder.of { libInfo =>
    // @formatter:off
    <pwslibrary version={libInfo.version.toString}>
      <books>
        {libInfo.bookRefs.map(_.encode)}
      </books>
    </pwslibrary>
    // @formatter:on
  }
}
