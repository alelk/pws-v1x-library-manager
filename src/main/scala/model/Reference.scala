package com.alelk.pws.library_manager
package model

import advxml.data.ValidatedNelThrow
import cats.syntax.all.*

sealed trait Reference

case class BibleRef(text: String) extends Reference

enum PsalmRefReason(val signature: String) {
  case Variation extends PsalmRefReason("variation")
}

object PsalmRefReason {
  def from(signature: String): ValidatedNelThrow[PsalmRefReason] =
    ValidatedNelThrow.fromOption(
      PsalmRefReason.values.find(_.signature == signature),
      IllegalArgumentException(s"Invalid psalm reference reason: $signature"))
}

case class PsalmRef(reason: PsalmRefReason, volume: Int, number: PsalmNumber) extends Reference
