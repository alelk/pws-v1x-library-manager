package com.alelk.pws.library_manager

import validator.PwsLibraryV1xValidator

import cats.data.Validated.*
import io.lemonlabs.uri.Url

import java.io.File

val pwsLibraryValidator = new PwsLibraryV1xValidator {}

@main
def main(args: String*): Unit = {
  args.toList match
    case List("validate", filename) =>
      val file = File(".").toURI.resolve(filename).toURL
      println(s"Analyse PWS library $file")
      val result = pwsLibraryValidator.validateLibrary(Url.parse(file.toString))
      result match
        case Valid(Nil) => println("Library valid")
        case Valid(errors) =>
          System.err.println(s"Library is invalid: \n  ${errors.mkString("\n  ")}")
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