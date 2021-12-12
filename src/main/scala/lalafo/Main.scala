package lalafo

import java.io.File
import scala.util.Success
import scala.util.Failure
import akka.actor.ActorSystem
import scala.sys.Prop

import scala.language.postfixOps

import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import sttp.model.UriInterpolator

trait Config {
  lazy val categryId: Int = 2046
  lazy val pageAmount = 10
}

object Main extends Config {

  val system = ActorSystem("Lalafo")

  val writerActor = system.actorOf(Props[WriterActor], "CsvWriter")

  val clientActor = system.actorOf(Props[ClientActor], "LalafoClient")

  def main(args: Array[String]): Unit = {

    implicit val timeout = Timeout(30 seconds)

    for (page <- 1 to pageAmount) {
      // asking for result from actor
      val clientActorFuture =
        (clientActor ? UriInterpolator.interpolate(
          StringContext(
            s"https://lalafo.kg/api/search/v3/feed/search?category_id=$categryId&expand=url&page=$page"
          )
        ))
          .mapTo[Vector[Apartment]]

      val apartments = Await.result(clientActorFuture, 30 seconds)

      apartments.map(apartment => {
        writerActor ! apartment
      })

    }
    system.stop(clientActor)

    val writerActorFuture = (writerActor ? true)
      .mapTo[Boolean]

    val ifWriterClosed = Await.result(writerActorFuture, 30 seconds)

    if (ifWriterClosed) {
      system.stop(writerActor)
    }

  }
}
