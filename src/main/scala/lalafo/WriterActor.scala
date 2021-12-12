package lalafo

import akka.actor.Actor

import java.io.File
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._

class WriterActor extends Actor {

  implicit val apartmentEncoder: RowEncoder[Apartment] =
    RowEncoder.encoder(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)(
      (a: Apartment) =>
        (
          a.id,
          a.countryId,
          a.city,
          a.cityId,
          a.title,
          a.description,
          a.url,
          a.lat,
          a.lon,
          a.price,
          a.currency,
          a.area,
          a.apartmentType,
          a.bedroomAmount
        )
    )

  val csv =
    new File("src/main/resources/apartments.csv").asCsvWriter[Apartment](
      rfc.withHeader(
        "id",
        "countryId",
        "city",
        "cityId",
        "title",
        "description",
        "url",
        "lat",
        "lon",
        "price",
        "currency",
        "area",
        "apartmentType",
        "bedroomAmount"
      )
    )

  def receive: Receive = {
    case apartment: Apartment => {
      csv.write(apartment)
    }
    case close: Boolean => {
      csv.close()
      sender() ! true
    }
  }
}
