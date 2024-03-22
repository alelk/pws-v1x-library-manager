package com.alelk.pws.library_manager
package validator

import model.{BibleRef, Psalm, Reference}

import advxml.data.ValidatedNelThrow
import cats.Monoid
import cats.data.Validated.Invalid
import cats.data.ValidatedNel
import cats.implicits.*
import io.lemonlabs.uri.Url

trait PwsLibraryV1xValidator extends PwsLibraryV1xLoader {

  //private implicit val booleanSemigroup: Semigroup[Boolean] = Semigroup.instance(_ && _)
  implicit val booleanMonoid: Monoid[Boolean] = new Monoid[Boolean] {
    def combine(x: Boolean, y: Boolean): Boolean = x && y
    def empty: Boolean = true
  }

  def validateLibrary(library: Url): ValidatedNelThrow[List[String]] =
    withLibrary(library) { lib =>
      lib.books.map { books =>
        books.map { book =>
            validateBook(book, book.url.toRelativeUrl.path.toString)
          }.combineAll
          .leftMap(_.toList)
          .leftSequence
          .collect { case Invalid(err) => err }
      }
    }

  private def validateBook(book: Book, address: String): ValidatedNel[String, Boolean] = {
    List(
      Either.cond(book.psalms.nonEmpty, true, "no psalms").toValidatedNel,
      book.psalms.andThen { psalms =>
        psalms.map(validatePsalm(_, address)).combineAll
      }
    ).combineAll.leftMap(_.map(msg => s"Book $address has wrong data: $msg"))
  }

  private def validatePsalm(psalm: Psalm, address: String): ValidatedNel[String, Boolean] = {
    List(
      Either.cond(psalm.name.nonEmpty, true, "name is empty").toValidatedNel,
      Either.cond(psalm.version.nonEmpty, true, "version is empty").toValidatedNel,
      Either.cond(psalm.numbers.nonEmpty, true, "no psalm numbers").toValidatedNel,
      psalm.numbers.map { num =>
        (
          Either.cond(num.number > 0, true, s"wrong number ${num.number} ($num)").toValidatedNel,
          Either.cond(num.bookEdition.nonEmpty, true, s"wrong book edition for number $num").toValidatedNel
        ).combineAll
      }.combineAll,
      Either.cond(psalm.text.nonEmpty, true, "no psalm text").toValidatedNel, {
        val ppNums = psalm.text.flatMap(_.numbers)
        val duplicates = ppNums.groupBy(identity).filter(_._2.size > 1).keys
        val missedNumbers = (ppNums.min to ppNums.max).diff(ppNums).toList
        (
          Either.cond(missedNumbers.isEmpty, true, s"missed psalm part numbers: ${missedNumbers.mkString(",")}").toValidatedNel,
          Either.cond(ppNums == ppNums.distinct, true, s"duplicate psalm part numbers: ${duplicates.mkString(",")}").toValidatedNel
        ).combineAll
      },
      psalm.text.map { psalmPart =>
        (
          Either.cond(psalmPart.numbers.nonEmpty, true, s"psalm part numbers is empty: ${psalmPart.text.split('\n').headOption.getOrElse("")}").toValidatedNel,
          Either.cond(psalmPart.text.nonEmpty, true, s"psalm part text is empty: ${psalmPart.numbers.mkString(",")}").toValidatedNel
        ).combineAll
      }.combineAll,
      psalm.references.map(validateReference).combineAll,
      psalm.tonalities.map(t => Either.cond(t.value.nonEmpty, true, "tonality is empty").toValidatedNel).combineAll,
      Either.cond(psalm.author.forall(_.nonEmpty), true, "author is empty string").toValidatedNel,
      Either.cond(psalm.translator.forall(_.nonEmpty), true, "author is empty string").toValidatedNel,
      Either.cond(psalm.composer.forall(_.nonEmpty), true, "author is empty string").toValidatedNel,
    ).combineAll.leftMap(_.map(msg => s"Psalm $address has wrong data: $msg"))
  }

  private def validateReference(ref: Reference) =
    ref match
      case BibleRef(text) => Either.cond(text.nonEmpty, true, "wrong bible reference: empty text").toValidatedNel
      case _ => s"wrong reference type: ${ref.getClass}".invalidNel
}
