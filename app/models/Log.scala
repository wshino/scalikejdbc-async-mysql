package models

import scalikejdbc._, async._, FutureImplicits._, SQLInterpolation._
import org.joda.time.DateTime
import scala.concurrent._
import ExecutionContext.Implicits.global
/**
 * Created with IntelliJ IDEA.
 * User: shinohara_wataru
 * Date: 13/09/26
 * Time: 18:52
 * To change this template use File | Settings | File Templates.
 */
case class Log(id: Long, createdAt: DateTime) extends ShortenedNames

object Log extends SQLSyntaxSupport[Log] with ShortenedNames {

  override val columnNames = Seq("id", "created_at")

  def apply(s: SyntaxProvider[Log])(rs: WrappedResultSet): Log = apply(s.resultName)(rs)

  def apply(s: ResultName[Log])(rs: WrappedResultSet): Log = new Log(
    id = rs.long(s.id),
    createdAt = rs.timestamp(s.createdAt).toDateTime
  )

  def opt(s: SyntaxProvider[Log])(rs: WrappedResultSet): Option[Log] = rs.longOpt(s.resultName.id).map(_ => apply(s.resultName)(rs))

  lazy val l = Log.syntax("l")

  //  def find = AsyncDB.withPool{ implicit ses =>
  //    withSQL {
  //          select.from(Log as l).orderBy(l.id).desc.limit(1)
  //    }.map(Log(l)).single().future()
  //  }

  def find(implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Option[Log]] = withSQL {
      select.from(Log as l).orderBy(l.id).desc.limit(1)
    }.map(Log(l))

  //  def create(createdAt: DateTime = DateTime.now)(
//    implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Log] = {
//    for {
//      id <- withSQL {
//        insert.into(Log).namedValues(column.createdAt -> createdAt)
//      }.updateAndReturnGeneratedKey.future()
//    } yield Log(id = id, createdAt = createdAt)
//  }


    def create(createdAt: DateTime = DateTime.now) = AsyncDB.localTx { implicit s =>
          for {
            id <- withSQL {
              insert.into(Log).namedValues(column.createdAt -> createdAt)
            }.updateAndReturnGeneratedKey.future()
          } yield Log(id = id, createdAt = createdAt)
    }
  /**
   *
   *   def create(name: String, createdAt: DateTime = DateTime.now)(
    implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Skill] = {
    for {
      id <- withSQL {
        insert.into(Skill).namedValues(column.name -> name, column.createdAt -> createdAt)
          .returningId // if you run this example for MySQL, please remove this line
      }.updateAndReturnGeneratedKey
    } yield Skill(id = id, name = name, createdAt = createdAt)
  }
   *
   *
   * */
}