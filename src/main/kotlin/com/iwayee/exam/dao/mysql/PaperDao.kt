package com.iwayee.exam.dao.mysql

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.mysqlclient.MySQLClient
import io.vertx.sqlclient.Tuple

object PaperDao : MyDao() {
  fun create(paper: JsonObject, action: (Long) -> Unit) {
    var fields = "`title`,`time`,`topics`,`config`";
    var sql = "INSERT INTO `paper` ($fields) VALUES (?,?,?,?)"

    client?.let {
      it.preparedQuery(sql).execute(Tuple.of(
              paper.getString("title"),
              paper.getInteger("time"),
              paper.getString("topics"),
              paper.getString("config")
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

  fun getPaperById(id: Int, action: (JsonObject?) -> Unit) {
    var fields = "`id`,`title`,`time`,`topics`,`config`";
    var sql = "SELECT $fields FROM `paper` WHERE id = ?"

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

  fun getPapers(page: Int, num: Int, action: (JsonArray) -> Unit) {
    var fields = "`id`,`title`,`time`,`topics`,`config`";
    var sql = "SELECT $fields FROM `paper` ORDER BY id DESC LIMIT ${(page - 1) * num},$num"

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

  fun getPapersByIds(ids: String, action: (JsonArray) -> Unit) {
    var fields = "`id`,`title`,`time`,`topics`,`config`";
    var sql = "SELECT $fields FROM `paper` WHERE id IN($ids)"

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

  fun updatePaperById(id: Int, paper: JsonObject, action: (Boolean) -> Unit) {
    val fields = ("title = ?, "
            + "time = ?, "
            + "topics = ?, "
            + "config = ?")
    var sql = "UPDATE `paper` SET $fields WHERE id = ?"

    client?.let {
      it.preparedQuery(sql).execute(Tuple.of(
              paper.getString("title"),
              paper.getInteger("time"),
              paper.getString("topics"),
              paper.getString("config"),
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
