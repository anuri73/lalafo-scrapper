package lalafo

final case class Apartment(
    id: Int,
    countryId: Int,
    city: String,
    cityId: Int,
    title: String,
    description: String,
    url: String,
    lat: Double,
    lon: Double,
    price: Int,
    currency: String
) {
  private lazy val titleItems = title.split(",")

  val apartmentType: String = titleItems(0)

  val bedroomAmount: Int = {
    ("""\d+""".r findFirstIn titleItems.lift(1).getOrElse(""))
      .getOrElse("0")
      .toInt
  }
  val area: Int = {
    ("""\d+""".r findFirstIn titleItems.lift(2).getOrElse(""))
      .getOrElse("0")
      .toInt
  }
}
