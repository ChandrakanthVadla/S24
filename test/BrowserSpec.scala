import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerTest


/**
  * Runs a browser based test against the application.
  *
  * http://doc.scalatest.org/3.0.0/index.html#org.scalatest.selenium.WebBrowser
  * http://www.scalatest.org/user_guide/using_selenium
  * https://www.playframework.com/documentation/latest/ScalaFunctionalTestingWithScalaTest#Testing-with-a-web-browser
  */
class BrowserSpec extends PlaySpec
  with OneBrowserPerTest
  with GuiceOneServerPerTest
  with HtmlUnitFactory {

  def $(str: String): Element = find(cssSelector(str)).getOrElse(throw new IllegalArgumentException(s"Cannot find $str"))

  "Application" should {

    "work from within a browser" in {
      System.setProperty("webdriver.gecko.driver", "/path/to/geckodriver")

      go to s"http://localhost:$port/"

      find("header-title").get.text must equal("Scout 24 Home Assingment — Advertisements database")
      $("section h1").text must equal("computers.list.title")

      click on id("searchbox")
      enter("xxx")
      submit()

      find(cssSelector("dl.error")) must equal(`None`)


      click on id("add")
      click on id("title")
        enter("Fiat Palio")
      singleSel("fuel").value = "Diesel"
      click on id("price")
        enter("100")
      click on id("mileageInput")
        enter("10")
      click on id("firstRegInput")
        enter("2001-01-01")
      submit()

       $("#pagination li.current a").text must equal ("Displaying 1 to 1 of 1")

      click on linkText("Fiat Palio")
    }
  }
}
