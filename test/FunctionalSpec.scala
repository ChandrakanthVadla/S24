
import akka.stream.Materializer
import controllers.HomeController
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._
import play.api.test.CSRFTokenHelper._

//import controllers.HomeController
//import org.scalatest.concurrent.ScalaFutures
//import play.api.test._
//import play.api.test.Helpers._
//import org.scalatestplus.play._
//import org.scalatestplus.play.guice._
//import play.api.test.CSRFTokenHelper._


class FunctionalSpec extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures {

  def dateIs(date: java.time.LocalDate, str: String) = {
    date.toString == str
  }

  def homeController = app.injector.instanceOf(classOf[HomeController])

  "HomeController" should {

    "redirect to the computer list on /" in {
      val result = homeController.index(FakeRequest())

      status(result) must equal(SEE_OTHER)
      redirectLocation(result) mustBe Some("/computers?s=1")
    }



    //running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {


    "create new computer" in {
      implicit lazy val materializer: Materializer = app.materializer

      val badResult = homeController.save(FakeRequest().withCSRFToken)

      status(badResult) must equal(BAD_REQUEST)

      val badDateFormat = homeController.save(
        FakeRequest().withFormUrlEncodedBody("id" -> "10", "title" -> "Maruti Suzuki", "fuel" -> "Gasoline",
          "price" -> "1", "new" -> "true", "mileage" -> "10", "first_registration" -> "2001-01-01" ).withCSRFToken
      )

      status(badDateFormat) must equal(SEE_OTHER)

      val result = homeController.save(
        FakeRequest().withFormUrlEncodedBody("id" -> "10", "title" -> "Tata Nano", "fuel" -> "Gasoline",
          "price" -> "1", "new" -> "true" , "mileage" -> "10", "first_registration" -> "2001-01-01").withCSRFToken
      )

      contentAsString(result) must equal("")

      status(result) must equal(SEE_OTHER)
      redirectLocation(result) mustBe Some("/computers?s=1")
      flash(result).get("success") mustBe Some("Advert Tata Nano has been created")

      val list = homeController.list(0, 1, "Tata Nano")(FakeRequest())

      status(list) must equal(OK)
     //contentAsString(list) must include("One Advert found")

    }


    "list computers on the the first page" in {
      val result = homeController.list(0, 2, "")(FakeRequest())

      status(result) must equal(OK)
      //contentAsString(result) must include("")
    }


    "filter computer by name" in {
      val result = homeController.list(0, 2, "")(FakeRequest())

      status(result) must equal(OK)
      contentAsString(result) must include("Displaying 1 to ")
    }


  }
}
