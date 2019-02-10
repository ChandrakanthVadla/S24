package v1.advert
import java.time.LocalDate

import javax.inject.{Inject, Provider}
import models.Fuel.Fuel
import models.{Advert, DBServices}
import play.api.MarkerContext
import play.api.libs.json._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps
/**
  * DTO for displaying advert information.
  */

case class AdvertResource(id: Option[Int] = None, title: String, fuel: Fuel,
                          price:Long, isNew:Boolean, mileage: Option[Int],
                          first_registration: Option[LocalDate])

object AdvertResource {

  /**
    * Mapping to write a AdvertResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[AdvertResource] {
    def writes(advert: AdvertResource): JsValue = {
      Json.obj(
        "id" -> advert.id,
        "title" -> advert.title,
        "fuel" -> advert.fuel,
        "price" -> advert.price,
        "new" -> advert.isNew,
        "mileage" -> advert.mileage.getOrElse("").toString,
        "first registration" -> advert.first_registration.getOrElse("").toString
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[ AdvertResource ]]
  */
class AdvertResourceHandler @Inject()(
                                       routerProvider: Provider[AdvertRouter],
                                       dbservices: DBServices,
                                       //advertRepository: AdvertRepositoryV1,
                                       //advertRepositoryModel: AdvertRepository
)(implicit ec: ExecutionContext) {

  def lookup(id: Int)(
    implicit mc: MarkerContext): Future[Option[AdvertResource]] = {
    val advertFuture = dbservices.select(id)
    advertFuture.map { maybePostData =>
      maybePostData.map { postData =>
        createAdvertResource(postData)
      }
    }
  }

  def find(implicit mc: MarkerContext): Future[Iterable[AdvertResource]] = {
    print("Inside find of AdvertResourceHandler")
    val modelAdvertList:Seq[models.Advert] = Await.result(dbservices.listAll(1) , Duration.Inf)
    Future.successful( (modelAdvertList.map(a => createAdvertResource(a))).toIterable )
  }
  def delete(id:Int) =  {

    dbservices.delete(id)
  }

  def add(advertInput: Advert)(
    implicit mc: MarkerContext): Future[Int] = {
    println(("Inside Handler with "))
    dbservices.add(advertInput)
  }
  private def createAdvertResource(a: Advert): AdvertResource = {
    AdvertResource( a.id, a.title, a.fuel, a.price, a.`new`, a.mileage, a.firstRegistration)
  }

}
