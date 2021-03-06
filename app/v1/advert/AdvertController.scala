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
      "new" -> boolean,
      "mileage" -> optional(number),
      "firstRegistration" -> optional(localDate("yyyy-MM-dd"))
    )(Advert.apply)(Advert.unapply)
  )


  def index(sortBy: String = "id"): Action[AnyContent] = AdvertAction.async { implicit request =>
    logger.trace("index: ")
    val sort = sortBy match {
      case "title" | "fuel" | "price" | "new" => sortBy
      case _ => "id"
    }
    advertResourceHandler.listAll(sort).map { response =>
      Ok(Json.toJson(response))
    }
  }


  def show(id: String): Action[AnyContent] = AdvertAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")

      val idInt:Int = if(!id.isEmpty && (id forall Character.isDigit)) id.toInt else -1

      advertResourceHandler.lookup(idInt).map{ response =>
        val re = response match {
          case Some(x) => Json.toJson(x)
          case None => Json.toJson("Id not Found")
        }
        Ok(Json.toJson(re) )
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
    processJsonPost()
  }

  private def processJsonPost[A]()(
    implicit request: AdvertRequest[A]): Future[Result] = {

    def failure(badForm: Form[Advert]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }


    def success(input: Advert) = {
      advertResourceHandler.add(input).map { ret =>
        Created(Json.toJson("success")).withHeaders()
      }
    }
    computerForm.bindFromRequest().fold(failure, success)
  }

  def update(id:Int): Action[AnyContent] = AdvertAction.async { implicit request =>
    logger.trace("process: ")
    processJsonPostUpdate(id)
  }

  private def processJsonPostUpdate[A](id:Int)(
    implicit request: AdvertRequest[A]): Future[Result] = {

    def failure(badForm: Form[Advert]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }


    def success(input: Advert) = {
      advertResourceHandler.update(id,input).map { ret =>
        Created(Json.toJson("success")).withHeaders()
      }
    }
    computerForm.bindFromRequest().fold(failure, success)
  }

}
