package com.alelk.pws.library_manager
package model

import io.lemonlabs.uri.RelativeUrl

import java.time.Year
import java.util.{Date, Locale}

case class BookInfo(version: Version,
                    name: String,
                    language: Locale,
                    displayName: String,
                    displayShortName: String,
                    edition: String,
                    psalmRefs: List[RelativeUrl],
                    releaseDate: Option[Year] = None,
                    description: Option[String] = None,
                    preface: Option[String] = None,
                    creators: List[String] = Nil,
                    editors: List[String] = Nil)
