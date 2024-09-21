package com.alelk.pws.library_manager
package v2x.converter

import model.{BookInfo, Psalm}
import v2x.model.BookV2

extension (b: BookInfo)
  def toBookV2(psalms: List[Psalm]): BookV2 =
    BookV2(version = b.version, name = b.name, locale = b.language, displayName = b.displayName,
      displayShortName = b.displayShortName, id = b.edition,
      songs =
        psalms
          .sortBy(_.numbers.find(_.bookEdition == b.edition).map(_.number).getOrElse(Int.MaxValue))
          .zipWithIndex.map { case (p, i) =>
            p.toPsalmV2(i + b.psalmStartId, b.language)
          },
      releaseDate = b.releaseDate, description = b.description, preface = b.preface, creators = b.creators,
      editors = b.editors)
