package com.alelk.pws.library_manager

import com.alelk.pws.library_manager.v2x.model.LibraryV2
import com.alelk.pws.library_manager.v2x.yaml_support.{BookV2YamlConverter, LibraryV2YamlConverter, PsalmV2YamlConverter}
import io.circe.yaml.Printer
import io.circe.yaml.Printer.StringStyle
import io.lemonlabs.uri.Url
import io.circe.syntax.*

import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

trait PwsLibraryV2Writer extends LibraryV2YamlConverter with BookV2YamlConverter with PsalmV2YamlConverter {

  private val printer = Printer(stringStyle = StringStyle.Plain, preserveOrder = true)

  def write(library: LibraryV2, outputFile: Url) = {
    library.books.map { bookInfo =>
      val bookUrl = URI.create(outputFile.toString).resolve(s"books/${bookInfo.book.signature}.pws2bk")
      println(s"Create book $bookUrl...")
      val bookYaml = printer.pretty(bookInfo.book.asJson)
      Files.write(Paths.get(bookUrl), bookYaml.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
      println(s"Book $bookUrl created.")
      bookUrl
    }
    val libraryYaml = printer.pretty(library.asJson)
    println(s"Create library $outputFile...")
    Files.write(Paths.get(URI.create(outputFile.toString)), libraryYaml.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    println(s"Library $outputFile created.")
  }

}
