package dao

import play.api.data._
import play.api.data.Forms._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json.Json
import models._

/** Data access object for Tasks of the user*/
class TaskDao(userId: Long) {

	def getById(id: Long): Task =
		DB.withConnection { implicit c =>
			SQL("SELECT * FROM tasks WHERE id = {id} AND user_id = {userId}").on(
				'userId -> userId,
				'id -> id).as(TaskDao.task.single)
		}

	def all(): List[Task] =
		DB.withConnection { implicit c =>
			SQL("SELECT * FROM tasks WHERE user_id = {userId} ORDER BY id DESC").on(
				'userId -> userId).as(TaskDao.task *)
		}

	/**
	 *  @param taskTemplate - a Task definition. The Id is ignored.
	 */
	def create(taskTemplate: Task): Task =
		DB.withConnection { implicit c =>
			val id = SQL("SELECT nextval('task_id_seq') id FROM dual").as(TaskDao.id.single)
			SQL("INSERT INTO tasks (id, status, text, user_id) VALUES ({taskId},{status}, {text}, {userId})").on(
				'taskId -> id,
				'text -> text,
				'status -> 0,
				'userId -> userId).
				executeUpdate()
			getById(id)
		}

	def delete(id: Long) {
		DB.withConnection { implicit c =>
			SQL("DELETE FROM tasks WHERE id = {id}").on(
				'id -> id).
				executeUpdate()
		}
	}

	import Task._

	def update(modifiedTask: Task) {
		DB.withConnection { implicit c ⇒
			val status: Int = modifiedTask.status
			SQL("UPDATE tasks SET status={status}, text={text}, priority={priority} WHERE id = {id}").on(
				'id -> modifiedTask.id,
				'text -> modifiedTask.text,
				'priority -> modifiedTask.priority.level,
				'status -> status).
				executeUpdate()
		}
	}

}

object TaskDao {
	val id = get[Long]("id")

	val task = {
		get[Long]("id") ~
			get[Int]("status") ~
			get[Int]("priority") ~
			get[String]("text") ~
			get[Long]("user_id") map {
				case id ~ status ~ priority ~ label ~ userId => Task(id, status, Priority(priority), label)
			}
	}

}