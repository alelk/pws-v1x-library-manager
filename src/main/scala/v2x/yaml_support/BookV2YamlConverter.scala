package com.alelk.pws.library_manager
package v2x.yaml_support

import v2x.model.BookV2

import io.circe.syntax.*
import io.circe.{Encoder, Json}

trait BookV2YamlConverter {
  this: PsalmV2YamlConverter =>

  implicit lazy val bookV2YamlEncoder: Encoder[BookV2] = (b: BookV2) =>
    Json.fromFields(List(
      "name" -> Some(b.name.asJson),
      "displayName" -> Some(b.displayName.asJson),
      "displayShortName" -> Some(b.displayShortName.asJson),
      "version" -> Some(b.version.toString.asJson),
      "locale" -> Some(b.locale.toString.asJson),
      "signature" -> Some(b.signature.asJson),
      "releaseDate" -> b.releaseDate.map(_.asJson),
      "description" -> b.description.map(_.asJson),
      "creators" -> (if (b.creators.isEmpty) None else Some(b.creators.asJson)),
      "editors" -> (if (b.editors.isEmpty) None else Some(b.editors.asJson)),
      "preface" -> b.preface.map(_.asJson),
      "psalms" -> Some(b.psalms.asJson),
    ).collect { case (k, Some(v)) => (k, v) })

}
