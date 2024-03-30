package com.alelk.pws.library_manager
package model

sealed trait PsalmPart {
  val numbers: Set[Int]
  val text: String
}

case class PsalmVerse(numbers: Set[Int], text: String) extends PsalmPart

object PsalmVerse {
  def apply(number: Int, text: String): PsalmVerse = PsalmVerse(Set(number), text)
}

case class PsalmChorus(numbers: Set[Int], text: String) extends PsalmPart

object PsalmChorus {
  def apply(number: Int, text: String): PsalmChorus = PsalmChorus(Set(number), text)
}
