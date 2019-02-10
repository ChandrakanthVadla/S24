package models

import java.time.LocalDate
import scala.language.{higherKinds, postfixOps}
import models.Fuel.Fuel


object Fuel extends Enumeration {
  type Fuel = Value
  val Diesel, Gasoline = Value
}

final case class Advert(
                        id: Option[Int],
                        title: String,
                        fuel: Fuel,
                        price: Int,
                        `new`: Boolean,
                        mileage: Option[Int],
                        firstRegistration: Option[LocalDate]
                      )

/**
  * Helper for pagination.
  */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

//final case class CarAdvert(carAds: Seq[Advert])
//final case class CommandResult(count: Int)
