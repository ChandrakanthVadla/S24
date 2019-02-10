package models

import scala.language.{higherKinds, postfixOps}

trait Dao[T, U[_]] {
  def sortingFields: Set[String]
  def createSchema(): U[Unit]
  def add(row: T): U[Int]
  def selectAll(page: Int, pageSize: Int, sort: Int, filter: String = "%"): U[Page[T]]
  def listAll(sort: String): U[Seq[T]]
  def select(id: Int): U[Option[T]]
  def update(id: Int, row: T): U[Int]
  def delete(id: Int) : U[Int]
}