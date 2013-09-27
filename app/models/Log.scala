package models

import scalikejdbc._, async._, FutureImplicits._, SQLInterpolation._
import org.joda.time.DateTime
import scala.concurrent._

/**
 * Created with IntelliJ IDEA.
 * User: shinohara_wataru
 * Date: 13/09/26
 * Time: 18:52
 * To change this template use File | Settings | File Templates.
 */
case class Log(id: Long, createdAt: DateTime)

object Log extends SQLSyntaxSupport[Log] with ShortenedNames {

  override val columnNames = Seq("id", "created_at")

  def apply(s: SyntaxProvider[Log])(rs: WrappedResultSet): Log = apply(s.resultName)(rs)

  def apply(s: ResultName[Log])(rs: WrappedResultSet): Log = new Log(
    id = rs.long(s.id),
    createdAt = rs.timestamp(s.createdAt).toDateTime
  )

//  def opt(s: SyntaxProvider[Log])(rs: WrappedResultSet): Option[Log] = rs.longOpt(s.resultName.id).map(_ => apply(s.resultName)(rs))

  lazy val l = Log.syntax("l")

  //  def find = AsyncDB.withPool{ implicit ses =>
  //    withSQL {
  //          select.from(Log as l).orderBy(l.id).desc.limit(1)
  //    }.map(Log(l)).single().future()
  //  }

  def find(implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Option[Log]] =
    withSQL {
      select.from(Log as l).orderBy(l.id).desc.limit(1)
    }.map(Log(l))


  def create(createdAt: DateTime = DateTime.now)(implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Log] = {
    for {
      id <- withSQL {
        insert.into(Log).namedValues(column.createdAt -> createdAt)
      }.updateAndReturnGeneratedKey.future()
    } yield Log(id = id, createdAt = createdAt)
  }


}

/*
*
* package models

import scalikejdbc._, async._, FutureImplicits._, SQLInterpolation._
import org.joda.time.DateTime
import scala.concurrent._

case class Skill(
    id: Long,
    name: String,
    createdAt: DateTime,
    deletedAt: Option[DateTime] = None) extends ShortenedNames {

  def save()(implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Skill] = Skill.save(this)(session, cxt)
  def destroy()(implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Unit] = Skill.destroy(id)(session, cxt)
}

object Skill extends SQLSyntaxSupport[Skill] with ShortenedNames {

  override val columnNames = Seq("id", "name", "created_at", "deleted_at")

  def apply(s: SyntaxProvider[Skill])(rs: WrappedResultSet): Skill = apply(s.resultName)(rs)
  def apply(s: ResultName[Skill])(rs: WrappedResultSet): Skill = new Skill(
    id = rs.long(s.id),
    name = rs.string(s.name),
    createdAt = rs.timestamp(s.createdAt).toDateTime,
    deletedAt = rs.timestampOpt(s.deletedAt).map(_.toDateTime)
  )

  def opt(s: SyntaxProvider[Skill])(rs: WrappedResultSet): Option[Skill] = rs.longOpt(s.resultName.id).map(_ => apply(s.resultName)(rs))

  lazy val s = Skill.syntax("s")

  private val isNotDeleted = sqls.isNull(s.deletedAt)

  def find(id: Long)(implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Option[Skill]] = withSQL {
    select.from(Skill as s).where.eq(s.id, id).and.append(isNotDeleted)
  }.map(Skill(s))

  def findAll()(implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[List[Skill]] = withSQL {
    select.from(Skill as s)
      .where.append(isNotDeleted)
      .orderBy(s.id)
  }.map(Skill(s))

  def countAll()(implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Long] = withSQL {
    select(sqls.count).from(Skill as s).where.append(isNotDeleted)
  }.map(rs => rs.long(1)).single.future.map(_.get)

  def findAllBy(where: SQLSyntax)(
    implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[List[Skill]] = withSQL {
    select.from(Skill as s)
      .where.append(isNotDeleted).and.append(sqls"${where}")
      .orderBy(s.id)
  }.map(Skill(s))

  def countBy(where: SQLSyntax)(implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Long] = withSQL {
    select(sqls.count).from(Skill as s).where.append(isNotDeleted).and.append(sqls"${where}")
  }.map(_.long(1)).single.future.map(_.get)

  def create(name: String, createdAt: DateTime = DateTime.now)(
    implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Skill] = {
    for {
      id <- withSQL {
        insert.into(Skill).namedValues(column.name -> name, column.createdAt -> createdAt)
          .returningId // if you run this example for MySQL, please remove this line
      }.updateAndReturnGeneratedKey
    } yield Skill(id = id, name = name, createdAt = createdAt)
  }

  def save(m: Skill)(implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Skill] = withSQL {
    update(Skill).set(column.name -> m.name).where.eq(column.id, m.id).and.isNull(column.deletedAt)
  }.update.future.map(_ => m)

  def destroy(id: Long)(implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Unit] = {
    update(Skill).set(column.deletedAt -> DateTime.now).where.eq(column.id, id)
  }

}

*
*
* */