package ch.makery.address.util

import scalikejdbc.* //library to connect the database
import ch.makery.address.model.Food

trait Database : //
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:myDB;create=true;"; //point to the database, if the database is not created it will auto create by set it to true
  // initialize JDBC driver & connection pool
  Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "me", "mine") //is a library for executing sql into the database; me is user, mine is password
  // ad-hoc session provider on the REPL
  given AutoSession = AutoSession //given - for all the autosession type implicit parameter u have autosession aas a value

object Database extends Database : //Object Database will carry all the properties
  def setupDB() = //setting up
    if (!hasDBInitialize) then
      Food.initializeTable()

  def hasDBInitialize : Boolean =
    DB getTable "Food" match
      case Some(x) => true //created
      case None => false //not there

