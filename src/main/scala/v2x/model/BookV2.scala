package com.alelk.pws.library_manager
package v2x.model

import model.Psalm

import java.util.Locale

case class BookV2(version: String,
                  name: String,
                  language: Locale,
                  displayName: String,
                  displayShortName: String,
                  edition: String,
                  psalmStartId: Int,
                  psalms: List[Psalm],
                  releaseDate: Option[String] = None,
                  description: Option[String] = None,
                  preface: Option[String] = None,
                  creators: List[String] = Nil,
                  editors: List[String] = Nil)
