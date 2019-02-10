package v1.advert

import javax.inject.Inject
import models.Fuel.Fuel
import models._
import play.api.Logger
import play.api.data.{Form, FormError, Forms}
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps


/**
  * Takes HTTP requests and produces JSON.
  */
class AdvertController @Inject()(cc: AdvertControllerComponents)(
  implicit ec: ExecutionContext)
  extends AdvertBaseController(cc) {

  private val logger = Logger(getClass)

  val fuelTypes: Seq[(String, String)] = Fuel.values.map(f => (f.toString, f.toString)).toSeq
  implicit def matchFilterFormat: Formatter[Fuel] = new Formatter[Fuel] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], models.Fuel.Value] =
      data.get(key)
        .map(Fuel.withName(_))
        .toRight(Seq(FormError(key, "error.required", Nil)))

    override def unbind(key: String, value: Fuel) =
      Map(key -> value.toString)
  }
  val computerForm = Form(
    mapping(
      "id" -> ignored(None: Option[Int]),
      "title" -> nonEmptyText,
      "fuel" -> Forms.of[Fuel],
      "price" -> number,
      "isNew" -> boolean,
      "mileage" -> optional(number),
      "firstRegistration" -> optional(localDate("yyyy-MM-dd"))
    )(Advert.apply)(Advert.unapply)
  )


  def index: Action[AnyContent] = AdvertAction.async { implicit request =>
    logger.trace("index: ")
    println("Inside index of Advert Controller")
    advertResourceHandler.find.map { posts =>
      Ok(Json.toJson(posts))
    }
  }


  def show(id: Int): Action[AnyContent] = AdvertAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      advertResourceHandler.lookup(id).map { advert =>
        Ok(Json.toJson(advert))
      }
  }

  def delete(id: Int): Action[AnyContent] = AdvertAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      val futureInt = advertResourceHandler.delete(id)

      futureInt.map(i => Ok(Json.toJson(i.toString + " adverts deleted")))
  }

  def add: Action[AnyContent] = AdvertAction.async { implicit request =>
    logger.trace("process: ")
    println("INSIDE Controller PROCESS")
    processJsonPost()
  }

  private def processJsonPost[A]()(
    implicit request: AdvertRequest[A]): Future[Result] = {

    def failure(badForm: Form[Advert]) = {
      println("INSIDE FAILURE")
      Future.successful(BadRequest(badForm.errorsAsJson))
    }


    def success(input: Advert) = {
      println("INSIDE SUCCESS new is ")
      advertResourceHandler.add(input).map { ret =>
        Created(Json.toJson("success")).withHeaders()
      }
    }
    computerForm.bindFromRequest().fold(failure, success)
  }

}
