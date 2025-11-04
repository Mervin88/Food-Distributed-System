package ch.makery.address.util

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}

object DateUtil {
  private val DATE_PATTERN = "yyyy-MM-dd"
  private val DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN)

  //convert LocalDate to string
  extension (date: LocalDate)
    def asString: String =
      if (date == null)
        return null;
      return DATE_FORMATTER.format(date);

  //parse string into LocalDate safely
  extension (data: String)
    def parseLocalDate: Option[LocalDate] =
      try
        Option(LocalDate.parse(data, DATE_FORMATTER))
      catch
        case _: DateTimeParseException => None

    //check if string is a valid date
    def isValidDate: Boolean =
      data.parseLocalDate.isDefined
}



