package com.iwayee.exam.dao.mysql

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.mysqlclient.MySQLClient
import io.vertx.sqlclient.Tuple

object SubjectDao : MyDao() {
  fun create(subject: JsonObject, action: (Long) -> Unit) {
    var fields = "`title`,`config`";
    var sql = "INSERT INTO `subject` ($fields) VALUES (?,?)"

    client?.let {
      it.preparedQuery(sql).execute(Tuple.of(
              subject.getString("title"),
              subject.getString("config")
      )) { ar ->
        var lastInsertId = 0L
        if (ar.succeeded()) {
          var rows = ar.result()
          lastInsertId = rows.property(MySQLClient.LAST_INSERTED_ID)
          println("Last Insert Id: $lastInsertId")
        } else {
          println("Failure: ${ar.cause().message}")
        }
        action(lastInsertId)
      }
    }
  }

  fun getSubjectById(id: Int, action: (JsonObject?) -> Unit) {
    var fields = "`id`,`title`, `config`";
    var sql = "SELECT $fields FROM `group` WHERE id = ?"

    client?.let {
      it.preparedQuery(sql).execute(Tuple.of(id)) { ar ->
        var jo: JsonObject? = null
        if (ar.succeeded()) {
          var rows = ar.result()
          for (row in rows) {
            jo = row.toJson()
          }
        } else {
          println("Failure: ${ar.cause().message}")
        }
        action(jo)
      }
    }
  }

  fun getSubjects(page: Int, num: Int, action: (JsonArray) -> Unit) {
    var fields = "`id`,`title`, `config`";
    var sql = "SELECT $fields FROM `subject` ORDER BY id DESC LIMIT ${(page - 1) * num},$num"

    client?.let {
      it.preparedQuery(sql).execute { ar ->
        var jr = JsonArray()
        if (ar.succeeded()) {
          var rows = ar.result()
          for (row in rows) {
            jr.add(row.toJson())
          }
        } else {
          println("Failure: ${ar.cause().message}")
        }
        action(jr)
      }
    }
  }

  fun updateSubjectById(id: Int, subject: JsonObject, action: (Boolean) -> Unit) {
    val fields = ("title = ?, "
            + "config = ?")
    var sql = "UPDATE `subject` SET $fields WHERE id = ?"

    client?.let {
      it.preparedQuery(sql).execute(Tuple.of(
              subject.getString("title"),
              subject.getString("config"),
              id
      )) { ar ->
        var ret = false
        if (ar.succeeded()) {
          ret = true
        } else {
          println("Failure: ${ar.cause().message}")
        }
        action(ret)
      }
    }
  }
}
