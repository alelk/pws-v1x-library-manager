package com.alelk.pws.library_manager
package v2x.yaml_support

import model.*
import v2x.model.PsalmV2

import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.syntax.*

trait PsalmV2YamlConverter {

  implicit protected lazy val psalmNumberListEncoder: Encoder[List[PsalmNumber]] = (ns: List[PsalmNumber]) =>
    Json.obj(ns.map { case PsalmNumber(book, num) => (book, num.asJson) }: _*)

  implicit protected lazy val psalmPartListEncoder: Encoder[List[PsalmPart]] = (ps: List[PsalmPart]) =>
    Json.fromString {
      val verses = ps.filter(_.isInstanceOf[PsalmVerse]).sortBy(_.numbers.min).zipWithIndex
      val choruses = ps.filter(_.isInstanceOf[PsalmChorus]).sortBy(_.numbers.min).zipWithIndex
      val versesRepeated = verses.exists(_._1.numbers.size > 1)
      val singleChorus = choruses.size == 1
      (verses ++ choruses).flatMap { case (part, partNum) =>
          part.numbers.map { num => (num, (part, partNum)) }
        }.sortBy(_._1)
        .map(_._2)
        .foldLeft(List[(PsalmPart, Int, Boolean)]()) {
          case (result, (p, num)) if result.map(_._1).contains(p) => result :+ ((p, num, false))
          case (result, (p, num)) => result :+ ((p, num, true))
        }
        .map {
          case (PsalmVerse(_, text), partNum, true) if versesRepeated =>
            // if at least one psalm verse is repeated in psalm, then add "Verse" prefix for each verse
            s"Verse ${partNum + 1}.\n$text"
          case (PsalmVerse(_, text), partNum, true) =>
            // if psalm verses are never repeated, then print only psalm part number (skip "Verse" prefix)
            s"${partNum + 1}.\n$text"
          case (_: PsalmVerse, partNum, false) =>
            // when second occurs of psalm part
            s"[Verse ${partNum + 1}]"
          case (PsalmChorus(_, text), partNum, true) if singleChorus =>
            // if only one chorus in psalm, then skip chorus number
            s"Chorus.\n$text"
          case (PsalmChorus(_, text), partNum, true) =>
            // if more than one chorus in psalm, then print chorus number
            s"Chorus ${partNum + 1}.\n$text"
          case (_: PsalmChorus, partNum, false) if singleChorus =>
            s"[Chorus]"
          case (_: PsalmChorus, partNum, false) =>
            s"[Chorus ${partNum + 1}]"
        }.mkString("\n\n")
    }


  implicit protected lazy val psalmReferenceEncoder: Encoder[Reference] = {
    case BibleRef(text) => Json.obj("type" -> "bibleRef".asJson, "value" -> text.asJson)
    case _ => throw new IllegalArgumentException("Unsupported reference type")
  }

  implicit protected lazy val tonalityListEncoder: Encoder[List[Tonality]] = (ts: List[Tonality]) => ts.map(_.value).asJson

  implicit lazy val psalmYamlEncoder: Encoder[PsalmV2] = (p: PsalmV2) =>
    Json.fromFields(List(
      "id" -> Some(p.id.asJson),
      "name" -> Some(p.name.asJson),
      "numbers" -> Some(p.numbers.asJson),
      "version" -> Some(p.version.toString.asJson),
      "text" -> Some(p.text.asJson),
      "tonalities" -> (if (p.tonalities.isEmpty) None else Some(p.tonalities.asJson)),
      "references" -> (if (p.references.isEmpty) None else Some(p.references.asJson)),
      "author" -> p.author.map(_.asJson),
      "composer" -> p.composer.map(_.asJson),
      "translator" -> p.translator.map(_.asJson),
    ).collect { case (k, Some(v)) => (k, v) })

}
