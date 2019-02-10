package v1.advert

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the AdvertResource controller.
  */
class AdvertRouter @Inject()(controller: AdvertController) extends SimpleRouter {
  val prefix = "/v1/adverts"
  override def routes: Routes = {


    case GET(p"/" ? q_?"sort=$sort") =>
    {
      controller.index(sort.getOrElse("id"))
    }
    case GET(p"/") =>
    {
      print("Inside GET/")
      controller.index()
    }
    case GET(p"/$id") =>
    {
      controller.show(id)
    }

    case POST(p"/") =>
    {
      controller.add
    }
    case PUT(p"/$id") =>
    {
      controller.update(id.toInt)
    }
    case DELETE(p"/$id") =>
    {
      controller.delete(id.toInt)
    }

  }

}
