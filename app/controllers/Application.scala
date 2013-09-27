package controllers

import play.api._
import play.api.mvc._
import models.Log

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {
    implicit req =>
      Async {
        Log.find.map {
          res =>
            Log.create().map {
              l =>
            }
            val id: Long = res map (r => r.id) getOrElse 0
            Ok(Map("id" -> id).toString())
        }
      }
  }


  //  def index = Action { implicit ses =>
  //    Ok("")
  //  }

}