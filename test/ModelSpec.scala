
import java.time.LocalDate

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.concurrent.Await
import scala.concurrent.duration.Duration



class ModelSpec extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures {

  import models._

  // -- Date helpers

  def dateIs(date: java.util.Date, str: String): Boolean = {
    new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str
  }

  // --

  def dbService: DBServices = app.injector.instanceOf(classOf[DBServices])

  "Computer model" should {

    "be retrieved by id" in {
      whenReady(dbService.select(21)) { maybeComputer =>
        val macintosh = maybeComputer.get

        macintosh.title must equal("Macintosh")
        macintosh.firstRegistration.value must matchPattern {
          case date: java.util.Date if dateIs(date, "1984-01-24") =>
        }
      }
    }


    "be updated if needed" in {

      val newRec: Advert = {
        Advert(Option(1), "Hummer", Fuel.Diesel, 100, `new` = false, Option(5), Option(LocalDate.of(2001, 10, 10)))
      }
      dbService.add(newRec)

      val row: Advert = Await.result(dbService.select(dbService.totalRows), Duration.Inf).get
      val rowNew = Advert(row.id, "New " + row.title, row.fuel, row.price - 10, row.`new`, row.mileage, row.firstRegistration)
      val rowUpdate = dbService.update(row.id.get, rowNew)


      whenReady(rowUpdate) { numOfUpdatesRows =>
        numOfUpdatesRows must equal(1)
      }

    }
  }
}
