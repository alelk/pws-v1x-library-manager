package com.alelk.pws.library_manager
package model

case class Psalm(version: String,
                 name: String,
                 numbers: List[PsalmNumber],
                 tonalities: List[Tonality] = Nil,
                 author: Option[String] = None,
                 translator: Option[String] = None,
                 composer: Option[String] = None)
