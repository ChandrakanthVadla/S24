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

      println("Thw port of the app is [",port,"]!!!!!!!!!!!!!!!!")
      go to s"http://localhost:$port/"

      find("header-title").get.text must equal("Scout 24 Home Assingment — Advertisements database")

      //find("section-title").get.text must not be empty

      //find(cssSelector(".current")) must equal(`None`)

      //click on $("#pagination li.next a")

      $("section h1").text must equal("computers.list.title")
      //click on linkText("Mini Cooper")

      click on id("searchbox")
      enter("xxx")
      submit()

      find(cssSelector("dl.error")) must equal(`None`)


      click on id("add")
/*
      click on id("title")
       enter("Volvo")

      click on id("fuel")
      enter("Diesel")
      click on id("price")
      enter("100")
      click on id("isNew")
      enter("true")
      //submit()

*/

           /// $("section h1").text must equal("Add New Advert")

    }
  }
}
