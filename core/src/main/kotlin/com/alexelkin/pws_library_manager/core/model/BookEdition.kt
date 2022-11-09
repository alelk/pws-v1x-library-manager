package com.alexelkin.pws_library_manager.core.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.lang.IllegalArgumentException

/**
 * Book Editions
 *
 * Created by Alex Elkin on 26.03.2015.
 */
enum class BookEdition(@get: JsonProperty val signature: String, val fileSignature: String, private val sortOrder: Int) {
  PV3300("PV3300", "m", 1),
  PV3055("PV3055", "f", 2),
  PV2555("PV2555", "b", 3),
  PV2001("PV2001", "w", 4),
  PV800("PV800", "o", 5),
  DERJIS_HRISTA("DerjisHrista", "dh", 10),
  PESN_HVALY("PesnHvaly", "ph", 11),
  YUNOST_IISUSU("YunostIisusu", "y", 12),
  DUHOVNY_PESNI("DuhovnyPesni", "dp", 13),
  TEBE_POYU_O_MOY_SPASITEL("TPMS", "tp", 14),
  GUSLI("Gusli", "g", 18),
  CHYMNS("CHymns", "h", 19),
  CPsalms("CPsalms", "p", 20),
  SDP("SDP", "s", 21),
  Tympan("Tympan", "t", 22),
  Kimval("Kimval", "k", 23),
  FCPSALMS("FCPsalms", "i", 24),
  SLAVIT_BEZ_ZAPINKI("SlavitBezZapinki", "z", 25),
  PVNL("PVNL", "v", 30),
  ZARYA("Zarja", "j", 31),
  SVIREL("Svirel", "d", 32),
  NNAPEVY("NNapevy", "n", 33),
  CHUDNY_KRAY("ChudnyKray", "c", 34),
  NPE("NPE", "e", 35),
  PESNI_GLUBINY("PesniGlubiny", "l", 36),
  PESNI_ANNY("PesniAnny", "a", 38),
  PSALMOVIVI("Psalmovivi", "up", 50),
  EVANGELSKI_PISNI("EvangelskiPisni", "ue", 51),
  PISNI_SPASENNYH("PisniSpasennyh", "us", 52);

  companion object {

    @JsonCreator
    fun bySignature(signature: String): BookEdition =
      values().find { it.signature.lowercase() == signature.lowercase() }
        ?: throw IllegalArgumentException("invalid book edition signature: $signature")
  }
}