package com.alelk.pws.library_manager
package model

import io.lemonlabs.uri.RelativeUrl

case class BookRef(preference: Int, reference: RelativeUrl)

case class LibraryInfo(version: String,
                       bookRefs: List[BookRef])
