package com.s24

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
//import akka.http.scaladsl.server.Directives.concat
//import akka.http.scaladsl.server.Route
//import cats.instances.future._
import com.typesafe.config.ConfigFactory
//import com.typesafe.scalalogging.StrictLogging
import slick.jdbc.PostgresProfile.api._


/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule
    with ScalaModule {

  val db = Database.forConfig("storage", ConfigFactory.load())
}
