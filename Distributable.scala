package ch.makery.address.model
import scalafx.beans.property.StringProperty


trait Distributable:
  def quantity: StringProperty
  def status: StringProperty
  def name: StringProperty
  
  def distribute(amount: Int): Either[String, String] =
    val pattern = """(\d+)([a-zA-Z]*)""".r
    val (currQty, unit) = quantity.value.trim match
      case pattern(numStr, unitStr) => (numStr.toIntOption.getOrElse(0), unitStr)
      case _ => (quantity.value.toIntOption.getOrElse(0), "")

    if amount <= 0 then Left("Invalid amount. Must be > 0")
    else if amount > currQty then Left(s"Not enough quantity. Only $currQty available.")
    else
      val newQty = currQty - amount
      quantity.value = s"$newQty$unit"
      if newQty == 0 then status.value = "Distributed"
      Right(s"$amount ${unit} of '${name.value}' distributed.")
