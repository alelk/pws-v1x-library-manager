package com.alelk.pws.library_manager
package v2x.yaml_support

import v2x.model.LibraryV2

import io.circe.syntax.*
import io.circe.{Encoder, Json}

trait LibraryV2YamlConverter {

  implicit lazy val libraryV2YamlEncoder: Encoder[LibraryV2] = lib =>
    Json.fromFields(List(
      "version" -> lib.version.toString.asJson,
      "books" -> lib.books.map { bookInfo =>
        Json.fromFields(List(
          "priority" -> bookInfo.priority.asJson,
          "bookRef" -> s"books/${bookInfo.book.signature}.pws2bk".asJson
        ))
      }.asJson
    ))

}
