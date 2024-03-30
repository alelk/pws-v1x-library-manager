package com.alelk.pws.library_manager
package validator

import model.{BibleRef, BookInfo, Psalm, PsalmNumber, PsalmRef, Reference}

import advxml.data.ValidatedNelThrow
import cats.Monoid
import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.implicits.*
import io.lemonlabs.uri.Url

import java.time.Year

trait PwsLibraryV1xValidator extends PwsLibraryV1xLoader {

  private implicit val booleanMonoid: Monoid[Boolean] = new Monoid[Boolean] {
    def combine(x: Boolean, y: Boolean): Boolean = x && y

    def empty: Boolean = true
  }

  def validateLibrary(library: Url): ValidatedNelThrow[List[String]] =
    withLibrary(library) { lib =>
      lib.books.map { books =>
        List(
          {
            val idRanges = books.map(b => b -> (b.info.psalmStartId to (b.info.psalmStartId + b.info.psalmRefs.size)))
            (for {
              (b1, r1) <- idRanges
              (b2, r2) <- idRanges if b2 != b1
            } yield {
              val duplicates = r1 intersect r2
              Either.cond(duplicates.isEmpty, true, s"psalm ids are not unique: duplicates in ${b1.info.edition} and ${b2.info.edition}: ${duplicates.take(20).mkString(",")}").toValidatedNel
            }).combineAll
          },
          books.map { book =>
            validateBook(book)
          }.combineAll
        ).combineAll match {
          case Valid(true) => List()
          case Valid(false) => List("Validation failed: unknown error")
          case Invalid(errors) => errors.toList
        }
      }
    }

  private[validator] def validateBook(book: Book): ValidatedNel[String, Boolean] = {
    val address = book.url.path.parts.lastOption.getOrElse(book.info.displayShortName)
    List(
      Either.cond(!book.info.name.isBlank, true, "name is blank").toValidatedNel,
      Either.cond(!book.info.displayShortName.isBlank, true, "display short name is blank").toValidatedNel,
      Either.cond(!book.info.displayName.isBlank, true, "display name is blank").toValidatedNel,
      Either.cond(!book.info.edition.isBlank, true, "edition is blank").toValidatedNel,
      Either.cond(book.info.psalmStartId > 0, true, s"psalm start id is less than 1: ${book.info.psalmStartId}").toValidatedNel,
      Either.cond(book.info.releaseDate.forall(_.isBefore(Year.now())), true, s"release date is in the future: ${book.info.releaseDate}").toValidatedNel,
      Either.cond(book.info.creators.forall(!_.isBlank), true, s"creator should not be blank").toValidatedNel,
      Either.cond(book.info.editors.forall(!_.isBlank), true, s"editor should not be blank").toValidatedNel,
      book.psalms.leftMap(_.map(e => e.getMessage)).andThen { psalms =>
        psalms.zipWithIndex.map { case ((psalm, psalmInfo), idx) => validatePsalm(psalm, psalmInfo.relativeUrl.toString, book.info) }.combineAll
      }
    ).combineAll.leftMap(_.map(msg => s"Book $address: $msg"))
  }

  private[validator] def validatePsalm(psalm: Psalm, address: String, bookInfo: BookInfo): ValidatedNel[String, Boolean] = {
    List(
      Either.cond(!psalm.name.isBlank, true, "name is empty").toValidatedNel,
      Either.cond(psalm.version.major <= 2, true, "major version is greater than 2").toValidatedNel,
      Either.cond(psalm.numbers.nonEmpty, true, "no psalm numbers").toValidatedNel,
      psalm.numbers.map { num =>
        (
          Either.cond(num.number > 0, true, s"number should be greater than 0: ${num.number} ($num)").toValidatedNel,
          Either.cond(num.number < 100000, true, s"number should be less than 100 000: ${num.number} ($num)").toValidatedNel,
          Either.cond(num.bookEdition.nonEmpty, true, s"wrong book edition for number $num").toValidatedNel
        ).combineAll
      }.combineAll,
      Either.cond(psalm.numbers.exists(_.bookEdition == bookInfo.edition), true, s"no psalm number for book edition ${bookInfo.edition}").toValidatedNel,
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
    ).combineAll.leftMap(_.map(msg => s"Psalm $address: $msg"))
  }

  private[validator] def validateReference(ref: Reference) =
    ref match
      case BibleRef(text) => Either.cond(text.nonEmpty, true, "wrong bible reference: empty text").toValidatedNel
      case PsalmRef(reason, volume, PsalmNumber(bookEdition, number)) =>
        (
          Either.cond(volume > 0, true, s"wrong psalm reference: volume should be greater than 0: $volume").toValidatedNel,
          Either.cond(volume <= 100, true, "wrong psalm reference: volume should be less or equal than 100: $volume").toValidatedNel,
          Either.cond(number > 0, true, s"wrong psalm reference: number should be greater than 0: $number").toValidatedNel,
          Either.cond(number < 100000, true, s"wrong psalm reference: number should be less than 100 000: $number").toValidatedNel,
          Either.cond(!bookEdition.isBlank, true, s"wrong psalm reference: book edition is blank").toValidatedNel
        ).combineAll
}
