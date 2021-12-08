package lalafo

import java.io.File
import scala.util.Success
import scala.util.Failure

case class Apartment(
    id: Int,
    countryId: Int,
    city: String,
    city_id: Int,
    title: String,
    description: String,
    url: String,
    lat: Double,
    lon: Double,
    price: Int,
    currency: String
)

case class LalafoResponse(
    items: List[Apartment]
)

object Main extends Config {

  import scala.concurrent.ExecutionContext.Implicits.global

  import sttp.client._
  import sttp.client.asynchttpclient.future._

  implicit val sttpBackend = AsyncHttpClientFutureBackend()

  import io.circe._
  import io.circe.generic.auto._
  import io.circe.parser._
  import io.circe.syntax._

  import java.io.File
  import kantan.csv._ 
  import kantan.csv.ops._
  import kantan.csv.generic._

  def main(args: Array[String]): Unit = {
    val response = basicRequest
      .get(
        uri"https://lalafo.kg/api/search/v3/feed/search?category_id=$categryId&expand=url&page=1"
      )
      .headers(headers)
      .send()

    response.onComplete {
      case Success(response) => {
        parse(response.body.getOrElse("[]")) match {
          case Left(parsingError) =>
            throw new IllegalArgumentException(
              s"Invalid JSON object: ${parsingError.message}"
            )
          case Right(json) =>
            val items = json.hcursor
              .downField("items")
              .values
              .getOrElse(Vector.empty[Json])

            val apartments = items.map(row =>
              new Apartment(
                row.hcursor.downField("id").as[Int].getOrElse(0),
                row.hcursor.downField("countryId").as[Int].getOrElse(0),
                row.hcursor.downField("city").as[String].getOrElse(""),
                row.hcursor.downField("city_id").as[Int].getOrElse(0),
                row.hcursor.downField("title").as[String].getOrElse(""),
                row.hcursor
                  .downField("description")
                  .as[String]
                  .getOrElse("")
                  .split('\n')
                  .map(_.trim.filter(_ >= ' '))
                  .mkString,
                row.hcursor.downField("url").as[String].getOrElse(""),
                row.hcursor.downField("lat").as[Double].getOrElse(0.0),
                row.hcursor.downField("lon").as[Double].getOrElse(0.0),
                row.hcursor.downField("price").as[Int].getOrElse(0),
                row.hcursor.downField("currency").as[String].getOrElse("")
              )
            )

            new File("src/main/resources/apartments.csv").writeCsv[Apartment](apartments, rfc)

            sttpBackend.close()
        }
      }
      case Failure(t) => println("An error has occurred: " + t.getMessage)
    }
  }
}

trait Config {
  lazy val categryId: Int = 2046
  lazy val countryId: String = "12"
  lazy val headers = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Accept-Language" -> "en-US,en;q=0.5",
    "Accept-Encoding" -> "gzip, deflate",
    "device" -> "pc",
    "country-id" -> countryId,
    "request-id" -> "react-client_b62b2a3c-5e7b-44c0-b406-f64a79aa6134",
    "Authorization" -> "Bearer",
    "user-hash" -> "9209cdf9-4d5f-46d3-9631-0d2e01b98095"
  )
}