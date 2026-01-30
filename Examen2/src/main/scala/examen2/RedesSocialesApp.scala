package examen2

import cats.effect._
import doobie._
import doobie.implicits._
import fs2.text
import fs2.io.file.{Files, Path}
import fs2.data.csv._
import fs2.data.csv.generic.semiauto._
import cats.implicits._
import java.time.LocalDate
import db.Database

object RedesSocialesApp extends IOApp.Simple {

  case class RedSocial(
                        `ID`: Int,
                        `Nombre_Usuario`: String,
                        `Plataforma`: String,
                        `Seguidores`: Int,
                        `Siguiendo`: Int,
                        `Publicaciones`: Int,
                        `Me_Gusta_Promedio`: Int,
                        `Comentarios_Promedio`: Int,
                        `Es_Influencer`: Boolean,
                        `Fecha_Registro`: String
                      )

  given CsvRowDecoder[RedSocial, String] =
    deriveCsvRowDecoder[RedSocial]

  def insert(rs: RedSocial): Update0 =
    sql"""
    INSERT INTO redes_sociales VALUES (
      ${rs.`ID`},
      ${rs.`Nombre_Usuario`},
      ${rs.`Plataforma`},
      ${rs.`Seguidores`},
      ${rs.`Siguiendo`},
      ${rs.`Publicaciones`},
      ${rs.`Me_Gusta_Promedio`},
      ${rs.`Comentarios_Promedio`},
      ${rs.`Es_Influencer`},
      ${java.sql.Date.valueOf(rs.`Fecha_Registro`)}
    )
  """.update

  val run: IO[Unit] = {
    Files[IO]
      .readAll(Path("src/main/resources/redes_sociales.csv"))
      .through(text.utf8.decode)
      .through(decodeUsingHeaders[RedSocial]())
      .evalMap(rs => insert(rs).run.transact(Database.xa))
      .compile
      .drain

  }
}