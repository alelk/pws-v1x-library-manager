package com.alelk.pws.library_manager

import advxml.data.ValidatedNelThrow
import cats.data.Validated.*
import io.lemonlabs.uri.Url

import java.io.File
import cats.implicits.*

import java.net.URI


val pwsLibLoader = new PwsLibraryV1xLoader {}
@main
def main(args: String*): Unit = {
  args.toList match
    case List("validate", filename) =>
      val file = File(".").toURI.resolve(filename).toURL
      println(s"Analyse PWS library $file")
      val result: ValidatedNelThrow[Boolean] = pwsLibLoader.withLibrary(Url.parse(file.toString)) { lib =>
        lib.books.andThen { books =>
          books.map { book =>
            book.psalms.map { psalms =>
              psalms.forall(!_.name.isBlank)
            }.forall(_ == true)
          }.forall(_ == true).valid
        }
      }
      result match
        case Valid(true) => println("Library valid")
        case Valid(false) => System.err.println("Library is invalid")
        case Invalid(errors) =>
          System.err.println(s"Error when loading library: \n  ${errors.map(_.getMessage).toList.mkString("\n  ")}")
    case List("--help") =>
      println(
        """
          |-- PWS Library v1.x manager --
          |Usage:
          |  validate [file] - validate specified PWS v1.x library
          |  --help - show this help
          |""".stripMargin)
    case _ => println("Invalid command. Run this program with '--help' argument to show usage information.")

}