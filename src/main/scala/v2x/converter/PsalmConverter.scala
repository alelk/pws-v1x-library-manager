package com.alelk.pws.library_manager
package v2x.converter

import model.Psalm
import v2x.model.PsalmV2

import java.util.Locale

extension (p: Psalm)
  def toPsalmV2(id: Int, locale: Locale): PsalmV2 =
    PsalmV2(id = id, version = p.version, name = p.name, numbers = p.numbers, text = p.text, locale = locale,
      references = p.references, tonalities = p.tonalities, author = p.author, translator = p.translator,
      composer = p.composer)