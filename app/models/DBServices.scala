package models



import java.sql.Timestamp
import java.time.{LocalDate, _}

import javax.inject.Inject
import models.Fuel.Fuel
import slick.ast.BaseTypedType
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcType

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.{higherKinds, postfixOps}



@javax.inject.Singleton
class DBServices @Inject() (implicit ec: ExecutionContext)  extends Dao[Advert, Future]
{

  val db = Database.forConfig("h2mem1")

  implicit val fuelEnumMapper: JdbcType[Fuel] with BaseTypedType[Fuel] =
    MappedColumnType.base[Fuel, String](_.toString, Fuel.withName)

  implicit val localDateColumnType: JdbcType[LocalDate] with BaseTypedType[LocalDate] = MappedColumnType
    .base[LocalDate, Timestamp](
    d => Timestamp.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant),
    d => d.toLocalDateTime.toLocalDate
  )

  class CarAdvert(tag: Tag) extends Table[Advert](tag, "adverts") {

    def id = column[Option[Int]]("id", O.PrimaryKey)
    def title = column[String]("title")
    def fuel = column[Fuel]("fuel")
    def price = column[Int]("price")
    def `new` = column[Boolean]("new")
    def mileage = column[Option[Int]]("mileage", O.Default(None))
    def firstRegistration = column[Option[LocalDate]]("first_registration")

    def * =
      (id, title, fuel, price, `new`, mileage, firstRegistration) <>
        (Advert.tupled, Advert.unapply)
  }

  TableQuery[CarAdvert].schema.create
  val carAds = TableQuery[CarAdvert]

  private val sorting = Map(
    "id" -> carAds.sortBy(_.id),
    "title" -> carAds.sortBy(_.title),
    "fuel" -> carAds.sortBy(_.fuel),
    "price" -> carAds.sortBy(_.price),
    "new" -> carAds.sortBy(_.`new`),
    "mileage" -> carAds.sortBy(_.mileage),
    "first_registration" -> carAds.sortBy(_.firstRegistration)
  )

  override  def createSchema(): Future[Unit] = {
    db.run(carAds.schema.create)
  }

  override
  def sortingFields: Set[String] = sorting.keys.toSet

  //def dropSchema(): FixedSqlAction[Unit, NoStream, Effect.Schema] = carAds.schema.drop
  def totalRows: Int = Await.result(db.run(carAds.map(_.id).length.result), Duration.Inf )
  /**
    * add a new advertisement
    */
  override  def add(ca: Advert): Future[Int] = {
    val row :Advert = Advert( Option(ca.id.getOrElse(totalRows+1)),ca.title,ca.fuel, ca.price,ca.`new`,ca.mileage,ca.firstRegistration)
    db.run(carAds += row)
  }

  override  def selectAll(page: Int = 0, pageSize: Int = 10, sort: Int, filter: String = "%"): Future[Page[Advert]] = {

    val offset = pageSize * page

    val sortBy:String = sort match {
      case 2 => "title"
      case 3 => "fuel"
      case 4 => "price"
      case 5 => "new"
      case _ => "id"
    }

    val items:Seq[Advert] = Await.result( sorting.get(sortBy) match {
      case Some(q) => db.run(q.drop(page * pageSize).take(pageSize).result)
      case None => Future.failed(new RuntimeException(s"Unknown sorting field: $sort"))
    } , Duration.Inf )


    Future.successful(Page(items, page, offset, totalRows))
  }

  override def listAll(sort: Int): Future[Seq[Advert]] = {
    val action = sort match {
      case 2 => carAds.sortBy(_.title).result
      case 3 => carAds.sortBy(_.fuel).result
      case 4 => carAds.sortBy(_.price).result
      case 5 => carAds.sortBy(_.`new`).result
      case _ => carAds.sortBy(_.id).result
    }
    db.run(action)
  }

  //override
  def select(id: Int): Future[Option[Advert]] =
    db.run(carAds.filter(_.id === id).take(1).result.headOption)

  override  def update(id: Int, row: Advert): Future[Int] = db.run(carAds.filter(_.id === id).update(row))

  override  def delete(id: Int): Future[Int] = db.run(carAds.filter(_.id === id).delete)

}
