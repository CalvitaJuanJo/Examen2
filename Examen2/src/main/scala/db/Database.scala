package db

import cats.effect._
import doobie._

object Database {

  val xa: Transactor[IO] =
    Transactor.fromDriverManager[IO](
      driver = "com.mysql.cj.jdbc.Driver",
      url = "jdbc:mysql://localhost:3306/examen2_db",
      user = "root",
      password = "Juan0007",
      logHandler = None
    )
}