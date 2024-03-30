package com.alelk.pws.library_manager
package v2x.model

import model.Version

case class BookV2Info(priority: Int, book: BookV2)

case class LibraryV2(version: Version, books: List[BookV2Info])
