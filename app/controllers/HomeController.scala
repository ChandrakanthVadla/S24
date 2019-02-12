package controllers

import javax.inject.Inject
import models.Fuel.Fuel
import models._
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formatter
import play.api.mvc._
import views._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps


/**
  * Controller for the advertisement wen interface
  */

class HomeController @Inject()(dbservices: DBServices,
                               cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) with CORSHandler {

  /**
    * This result directly redirect to the application home.
    */
  val Home: Result = Redirect(routes.HomeController.list(0, 1))
  val fuelTypes = Fuel.values.map(f => (f.toString, f.toString)).toSeq
  val computerForm = Form(
    mapping(
      "id" -> ignored(None: Option[Int]),
      "title" -> nonEmptyText,
      "fuel" -> Forms.of[Fuel],
      "price" -> number,
      "isNew" -> boolean,
      "mileage" -> optional(number),
      "first_registration" -> optional(localDate("yyyy-MM-dd"))
    )(Advert.apply)(Advert.unapply)
  )

  /**
    * Describe the advertisement form (used in both edit and create screens).
    */
  implicit def matchFilterFormat: Formatter[Fuel] = new Formatter[Fuel] {

    override def bind(key: String, data: Map[String, String]) =
      data.get(key)
        .map(Fuel.withName(_))
        .toRight(Seq(FormError(key, "error.required", Nil)))

    override def unbind(key: String, value: Fuel) =
      Map(key -> value.toString)
  }
  private val logger = play.api.Logger(this.getClass)

  // -- Actions

  /**
    * Handle default path requests, redirect to computers list
    */
  def index = Action {
    println("Entred The Index")
    Home
  }

  /**
    * Display the paginated list of advertisements.
    *
    * @param page    Current page number (starts from 0)
    * @param orderBy Column to be sorted
    * @param filter  Filter applied on advertisement titles
    */
  def list(page:Int = 0, orderBy:Int = 1, filter:String = "") = Action.async { implicit request =>

    println("Inside The list function")

    dbservices.selectAll(page, 10, orderBy, filter).map { page =>
      Ok(html.list(page, orderBy, filter))
    }
  }

  /**
    * Display the 'new advertisement form'.
    */
  def create = Action.async { implicit request =>
    Future.successful(Ok(html.createForm(computerForm, fuelTypes)))
  }

  /**
    * Display the 'edit form' of a existing advertisement.
    *
    * @param id Id of the advertisement to edit
    */

  def edit(id: Int) = Action.async {
    implicit request =>
      dbservices.select(id).flatMap {
        case Some(advert) => {
          Future.successful(Ok(html.editForm(id, computerForm.fill(advert), fuelTypes)))
        }

        case other =>
          Future.successful(NotFound)
      }
  }


  /**
    * Handle the 'edit form' submission
    *
    * @param id Id of the advertisement to edit
    */

  def update(id: Int) = Action.async { implicit request =>
    computerForm.bindFromRequest.fold(
      formWithErrors => {
        logger.warn(s"form error: $formWithErrors")
        Future.successful(BadRequest(html.editForm(id, formWithErrors, fuelTypes)))

      },
      a => {
        val data: Advert = Advert(Option(id), a.title, a.fuel, a.price, a.`new`, a.mileage, a.firstRegistration)
        dbservices.update(id, data).map { _ =>
          Home.flashing("success" -> "Advert %s has been updated".format(data.title))
        }
      }
    )
  }


  /**
    * Handle the 'new advertisement form' submission.
    */
  def save = Action.async { implicit request =>
    computerForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(html.createForm(formWithErrors, fuelTypes)))
      }
      ,
      a => {
        //val data: Advert = Advert(Option(dbservices.totalRows + 1), a.title, a.fuel, a.price, a.`new`, a.mileage, a.firstRegistration)
        dbservices.add(a).map { _ =>
          Home.flashing("success" -> "Advert %s has been created".format(a.title))
        }
      }
    )
  }

  /**
    * Handle computer deletion.
    */

  def delete(id: Int) = Action.async {
    dbservices.delete(id).map { _ =>
      Home.flashing("success" -> "Computer has been deleted")
    }
  }


}
