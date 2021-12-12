package lalafo

import akka.actor.Actor
import sttp.model.UriInterpolator
import sttp.model.Uri

trait RequestConfig {
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

class ClientActor extends Actor with RequestConfig {

  import io.circe._
  import io.circe.generic.auto._
  import io.circe.parser._
  import io.circe.syntax._

  def receive: Receive = {

    case route: Uri => {

      import sttp.client._
      import sttp.client.asynchttpclient.future._

      implicit val sttpBackend = HttpURLConnectionBackend()

      val response = basicRequest
        .get(route)
        .headers(headers)
        .send()

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

          val apartments = items.map(row => {
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
              row.hcursor.downField("lng").as[Double].getOrElse(0.0),
              row.hcursor.downField("price").as[Int].getOrElse(0),
              row.hcursor.downField("currency").as[String].getOrElse("")
            )
          })

          sender() ! apartments
      }

      sttpBackend.close()
    }
  }
}
