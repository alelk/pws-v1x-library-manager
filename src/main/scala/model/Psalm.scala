package com.alelk.pws.library_manager
package model

import java.util.Locale

case class Psalm(version: String,
                 name: String,
                 numbers: List[PsalmNumber],
                 text: List[PsalmPart],
                 references: List[Reference] = Nil,
                 tonalities: List[Tonality] = Nil,
                 author: Option[String] = None,
                 translator: Option[String] = None,
                 composer: Option[String] = None)
