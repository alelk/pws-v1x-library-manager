package com.alelk.pws.library_manager
package v2x.model

import model.{PsalmNumber, PsalmPart, Reference, Tonality, Version}

import java.util.Locale

case class PsalmV2(id: Int,
                   version: Version,
                   name: String,
                   numbers: List[PsalmNumber],
                   text: List[PsalmPart],
                   locale: Locale,
                   references: List[Reference] = Nil,
                   tonalities: List[Tonality] = Nil,
                   author: Option[String] = None,
                   translator: Option[String] = None,
                   composer: Option[String] = None)
