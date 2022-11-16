package com.alelk.pws.library_manager
package model

sealed trait PsalmPart {
  val numbers: Set[Int]
  val text: String
}

case class PsalmVerse(numbers: Set[Int], text: String) extends PsalmPart

case class PsalmChorus(numbers: Set[Int], text: String) extends PsalmPart
