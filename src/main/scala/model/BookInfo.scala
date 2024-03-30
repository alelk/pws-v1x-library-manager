package com.alelk.pws.library_manager
package model

import io.lemonlabs.uri.RelativeUrl

import java.time.Year
import java.util.Locale

case class BookInfo(version: Version = Version(0, 1),
                    name: String,
                    language: Locale = Locale.forLanguageTag("en"),
                    displayName: String,
                    displayShortName: String,
                    edition: String,
                    psalmStartId: Int = 1,
                    psalmRefs: List[RelativeUrl] = Nil,
                    releaseDate: Option[Year] = None,
                    description: Option[String] = None,
                    preface: Option[String] = None,
                    creators: List[String] = Nil,
                    editors: List[String] = Nil)
