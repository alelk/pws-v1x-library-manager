package com.alelk.pws.library_manager
package model

case class Psalm(version: Version = Version(0, 1),
                 name: String,
                 numbers: List[PsalmNumber],
                 text: List[PsalmPart],
                 references: List[Reference] = Nil,
                 tonalities: List[Tonality] = Nil,
                 author: Option[String] = None,
                 translator: Option[String] = None,
                 composer: Option[String] = None)
