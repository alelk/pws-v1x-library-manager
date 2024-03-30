package com.alelk.pws.library_manager
package validator

import model.{BookInfo, Psalm, PsalmNumber, PsalmVerse}

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import com.github.dwickern.macros.NameOf.nameOf
import io.lemonlabs.uri.Url
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.shouldBe

class PwsLibraryV1xValidatorTest extends AnyFlatSpec with PwsLibraryV1xValidator {

  private val bookInfo = BookInfo(name = "Book1", displayName = "book1", displayShortName = "b1", edition = "b1")

  nameOf(validatePsalm) should "return Invalid when psalm has no name" in {
    val r = validatePsalm(Psalm(name = " ", numbers = List(PsalmNumber("b1", 1)), text = List(PsalmVerse(1, "verse 1"))), "psalm 1", bookInfo)
    r shouldBe Invalid(NonEmptyList("Psalm psalm 1: name is empty", Nil))
  }

  nameOf(validateLibrary) should "validate v1x library and return no issues" in {
    val libFile = Url.parse(classOf[PwsLibraryV1xLoaderTest].getClassLoader.getResource("library-examples-full/content-ru.pwslib").toString)
    val r = validateLibrary(libFile)
    r shouldBe Valid(List())
  }

  it should "validate v1x library and return issues" in {
    val libFile = Url.parse(classOf[PwsLibraryV1xLoaderTest].getClassLoader.getResource("library-validation-examples/lib-validation-errors.pwslib").toString)
    val r = validateLibrary(libFile)
    r shouldBe Valid(List(
      "psalm ids are not unique: duplicates in b1 and PV3300: 2,3",
      "psalm ids are not unique: duplicates in PV3300 and b1: 2,3",
      "Book book1.pwsbk: name is blank",
      "Book book1.pwsbk: display short name is blank",
      "Book book1.pwsbk: display name is blank",
      "Book book1.pwsbk: creator should not be blank",
      "Book book1.pwsbk: editor should not be blank",
      "Book book1.pwsbk: Psalm book1/p1.pslm: name is empty",
      "Book book1.pwsbk: Psalm book1/p1.pslm: no psalm number for book edition b1",
      "Book book1.pwsbk: Psalm book1/p1.pslm: psalm part text is empty: 5",
      "Book book1.pwsbk: Psalm book1/p2.pslm: no psalm number for book edition b1",
      "Book book1.pwsbk: Psalm book1/p2.pslm: duplicate psalm part numbers: 5,7",
      "Book book1.pwsbk: Psalm book1/p2.pslm: tonality is empty",
      "Book book2.pwsbk: release date is in the future: Some(2900)",
      "Book book2.pwsbk: Psalm book2/p1.pslm: no psalm number for book edition PV3300",
      "Book book2.pwsbk: Psalm book2/p1.pslm: duplicate psalm part numbers: 7",
      "Book book2.pwsbk: Psalm book2/p1.pslm: wrong bible reference: empty text",
      "Book book2.pwsbk: Psalm book2/p1.pslm: tonality is empty"))
  }
}
