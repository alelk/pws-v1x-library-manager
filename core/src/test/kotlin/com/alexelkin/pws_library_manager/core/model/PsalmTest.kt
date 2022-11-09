package com.alexelkin.pws_library_manager.core.model

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.io.File

class PsalmTest : StringSpec({
  val modelMapper = XmlMapper(JacksonXmlModule()).registerModule(kotlinModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  "psalm can be deserialized from xml" {
    File(Psalm::class.java.getResource("/psalm-examples")?.file ?: throw IllegalArgumentException("No such file found"))
      .also { f ->
        f.exists() shouldBe true
      }.listFiles().also {
        it shouldNotBe null
        it!!.size shouldBeGreaterThan 0
      }!!.forEach {
        val psalm = modelMapper.readValue(it, Psalm::class.java)
        println(psalm)
        psalm.name.length shouldBeGreaterThan 0
        psalm.numbers.size shouldBeGreaterThan 0
        psalm.numbers.forEach { number ->
          number.edition shouldBeIn BookEdition.values()
          number.number shouldBeGreaterThan 0
        }
      }
  }

})