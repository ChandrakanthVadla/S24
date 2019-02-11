
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
      redirectLocation(result) mustBe Some("/computers")
    }


    "list computers on the the first page" in {
      val result = homeController.list(0, 2, "")(FakeRequest())

      status(result) must equal(OK)
      contentAsString(result) must include("574 computers found")
    }


    "filter computer by name" in {
      val result = homeController.list(0, 2, "Apple")(FakeRequest())

      status(result) must equal(OK)
      contentAsString(result) must include("13 computers found")
    }

    //running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {


    "create new computer" in {
      implicit lazy val materializer: Materializer = app.materializer

      val badResult = homeController.save(FakeRequest().withCSRFToken)

      status(badResult) must equal(BAD_REQUEST)

      val badDateFormat = homeController.save(
        FakeRequest().withFormUrlEncodedBody("title" -> "FooBar", "fuel" -> "Gasoline", "price" -> "1", "new" -> "true" ).withCSRFToken
      )


      status(badDateFormat) must equal(BAD_REQUEST)
      contentAsString(badDateFormat) must include("""<option value="1" selected="selected">Apple Inc.</option>""")
      contentAsString(badDateFormat) must include("""<input type="text" id="introduced" name="introduced" value="badbadbad" """)
      contentAsString(badDateFormat) must include("""<input type="text" id="name" name="name" value="FooBar" """)


      val result = homeController.save(
        FakeRequest().withFormUrlEncodedBody("title" -> "FooBar", "fuel" -> "Gasoline", "price" -> "1", "new" -> "true").withCSRFToken
      )

      status(result) must equal(SEE_OTHER)
      redirectLocation(result) mustBe Some("/computers")
      flash(result).get("success") mustBe Some("Computer FooBar has been created")

      val list = homeController.list(0, 2, "FooBar")(FakeRequest())

      status(list) must equal(OK)
      contentAsString(list) must include("One computer found")

    }
  }
}
