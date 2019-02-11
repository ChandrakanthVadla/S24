
import java.time.LocalDate

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
//import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


class ModelSpec extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures {

  import models._

  // -- Date helpers

  def dateIs(date: java.time.LocalDate, str: String): Boolean = {
    date.toString == str
  }

  def dbService: DBServices = app.injector.instanceOf(classOf[DBServices])

  "Advertisement model " should {

    var id = 0
    dbService.createSchema().onComplete {
      case Failure(e) => e.printStackTrace()
      case Success(_) => {
        id = dbService.totalRows+1
        val newRec: Advert = Advert(Option(id), "Mini Cooper", Fuel.Diesel, 100, `new` = false, Option(5), Option(LocalDate.of(2001, 10, 10)))
        dbService.add(newRec).onComplete {
          case Failure(exception) => exception.printStackTrace
          case Success(value) => println("success", value)
        }
      }
    }

    "be retrieved by id" in {
      whenReady(dbService.select(id)) {
        case None =>
        case Some(someAd) => {
          someAd.title must equal("Mini Cooper")
          someAd.firstRegistration.value must matchPattern {
            case date: java.time.LocalDate if dateIs(date, "2001-10-10") =>
          }
        }
      }
    }


    "be updated if needed" in {

      val lastRec = Await.result(dbService.select(id), Duration.Inf).get
      val newRec = Advert(lastRec.id, "Mega Cooper", lastRec.fuel, lastRec.price - 10, lastRec.`new`, lastRec.mileage, lastRec.firstRegistration)
      val rowUpdate = dbService.update(lastRec.id.get, newRec)


      whenReady(rowUpdate) { numOfUpdatesRows =>
        numOfUpdatesRows must equal(1)
      }

      whenReady(dbService.select(lastRec.id.get)) { mayBeUpdatedRec =>
        val updatedRec = mayBeUpdatedRec.get
        updatedRec.id.get must equal(newRec.id.get)
        updatedRec.title must equal(newRec.title)
      }
    }


  }
}
