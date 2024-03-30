package com.alelk.pws.library_manager

import v2x.converter.toBookV2
import v2x.model.{BookV2Info, LibraryV2}
import v2x.yaml_support.{BookV2YamlConverter, PsalmV2YamlConverter}
import validator.PwsLibraryV1xValidator

import advxml.data.ValidatedNelThrow
import cats.data.NonEmptyList
import cats.implicits.*
import io.lemonlabs.uri.Url

trait PwsLibraryV1xToV2xConverter extends PwsLibraryV1xLoader with PwsLibraryV1xValidator with BookV2YamlConverter with PsalmV2YamlConverter {

  def convert(url: Url): ValidatedNelThrow[LibraryV2] =
    validateLibrary(url).andThen {
      case Nil => true.validNel
      case errors => NonEmptyList.fromListUnsafe(errors.map(new UnsupportedOperationException(_))).invalid
    }.andThen { _ =>
      withLibrary(url) { library =>
        library.books.andThen { books =>
          books.map { book =>
            book.psalms.map { psalms =>
              book.info.toBookV2(psalms.map(_._1))
            }
          }.sequence
        }.map { booksV2 =>
          val booksV2Info = booksV2.zip(library.info.bookRefs).map((book, ref) => BookV2Info(ref.preference, book))
          LibraryV2(library.info.version, booksV2Info)
        }
      }
    }
}
