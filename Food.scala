package ch.makery.address.model

import scalafx.beans.property.{ObjectProperty, StringProperty}
import ch.makery.address.util.Database
import ch.makery.address.util.DateUtil.*
import scalikejdbc.*
import scala.util.{Failure, Try}
import java.time.LocalDate
import scala.language.postfixOps

class Food(Name: String, Quantity: String, Category: String, ExpiryDate: String, Status: String) extends Database with Distributable:
  def this () = this (null, null, null, null, null)
  val name = new StringProperty(Name)
  val quantity = new StringProperty(Quantity)
  val category = new StringProperty(Category)
  val expiryDate  = ObjectProperty[LocalDate](
    ExpiryDate.parseLocalDate.getOrElse(LocalDate.now())
    )

  expiryDate.onChange { (_, _, _) =>
    normalizeStatus()
  }
  val status = new StringProperty(Status)

  def save(): Try[Int]=
    if (!isExist) then
      Try(DB autoCommit { implicit session =>
        sql"""
          insert into Food (name, quantity, category, expiryDate, status)
          values (${name.value}, ${quantity.value}, ${category.value}, ${expiryDate.value}, ${status.value})
        """.update.apply()
        })
    else
      Try(DB autoCommit { implicit session =>
        sql"""
          update Food set
             quantity = ${quantity.value},
             category = ${category.value},
             expiryDate = ${expiryDate.value},
             status = ${status.value}
          where name = ${name.value}
        """.update.apply()
      })

  def delete(): Try[Int] =
    if isExist then
      Try(DB autoCommit { implicit session =>
        sql"""
          delete from Food where name = ${name.value}
        """.update.apply()
      })
    else
      Failure(new Exception("Food record does not exist"))

  // Expiry checks
  def isExist: Boolean =
    DB readOnly { implicit session =>
      sql"""
        select * from Food where name = ${name.value}
      """.map(_.string("name")).single.apply()
    } match
      case Some(x) => true
      case None => false

  def isExpired: Boolean =
    expiryDate.value.isBefore(LocalDate.now())

  def isExpiringSoon(days: Int = 5): Boolean =
    !isExpired && !expiryDate.value.isAfter(LocalDate.now().plusDays(days))

  // Normalize food status automatically if expired
  def normalizeStatus(): Unit =
    if (isExpired) {
      if (status.value == "Available") {
        status.value = "Expired"
        save()
      }
    } else {
      if (status.value == "Expired") {
        status.value = "Available"
        save()
      }
    }


object Food extends Database:
  def apply(nameS: String, quantityS: String, categoryS: String, expiryS: String, statusS: String): Food =
    new Food(nameS, quantityS, categoryS, expiryS, statusS)


  def initializeTable(): Unit =
    DB autoCommit { implicit session =>
    sql"""
      create table Food (
      id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
      name varchar(100),
      quantity varchar(10),
      category varchar(50),
      expiryDate varchar(30),
      status varchar(30)
     )
      """.execute.apply()
    }

  // Load all Foods from DB
  def getAllFoods: List[Food] =
    DB readOnly { implicit session =>
      sql"select * from food"
        .map(rs => Food(
          rs.string("name"),
          rs.string("quantity"),
          rs.string("category"),
          rs.string("expiryDate"),
          rs.string("status")
        )).list.apply()
    }

  // Normalize all Food records (set expired ones automatically)
  def normalizeAll(foods: Iterable[Food]): Unit =
    foods.foreach(_.normalizeStatus())