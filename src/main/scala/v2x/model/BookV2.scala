package com.alelk.pws.library_manager
package v2x.model

import model.Version

import java.time.Year
import java.util.Locale

case class BookV2(version: Version,
                  name: String,
                  locale: Locale,
                  displayName: String,
                  displayShortName: String,
                  signature: String,
                  psalms: List[PsalmV2],
                  releaseDate: Option[Year] = None,
                  description: Option[String] = None,
                  preface: Option[String] = None,
                  creators: List[String] = Nil,
                  editors: List[String] = Nil)
