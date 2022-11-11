package com.alexelkin.pws_library_manager.core.model

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.io.File

class LibraryTest : StringSpec({
  val xmlMapper = XmlMapper(JacksonXmlModule()).registerModule(kotlinModule())

  "library can be deserialized from xml" {
    File(Psalm::class.java.getResource("/library-examples")?.file ?: throw IllegalArgumentException("No such file found"))
      .also { f ->
        f.exists() shouldBe true
      }.listFiles().also {
        it shouldNotBe null
        it!!.size shouldBeGreaterThan 0
      }!!.forEach {
        val lib = xmlMapper.readValue(it, Library::class.java)
        println(lib)
        lib.version.isBlank() shouldBe false
      }
  }
})