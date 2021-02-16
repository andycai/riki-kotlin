package com.iwayee.exam.dao.mysql

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.mysqlclient.MySQLClient
import io.vertx.sqlclient.Tuple

object ExamDao : MyDao() {
  fun create(act: JsonObject, action: (Long) -> Unit) {
    var fields = "`type`,`paper_id`,`time`,`title`,`status`,`begin_at`";
    var sql = "INSERT INTO `exam` ($fields) VALUES (?,?,?,?,?)"

    client?.let {
      it.preparedQuery(sql).execute(Tuple.of(
              act.getInteger("type"),
              act.getInteger("paper_id"),
              act.getInteger("time"),
              act.getString("title"),
              act.getInteger("status"),
              act.getString("begin_at")
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

  fun getExamById(id: Int, action: (JsonObject?) -> Unit) {
    var fields = "`id`,`type`,`paper_id`,`time`,`title`,`status`,`begin_at`";
    var sql = "SELECT $fields FROM `exam` WHERE id = ?"

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

  fun getExamsByType(type: Int, status: Int, page: Int, num: Int, action: (JsonArray) -> Unit) {
    var fields = "`id`,`type`,`paper_id`,`time`,`title`,`status`,`begin_at`";
    var sql = "SELECT $fields FROM `exam` WHERE `type` = ? AND `status` = ? ORDER BY `id` DESC LIMIT ${(page - 1) * num},$num"

    client?.let {
      it.preparedQuery(sql).execute(Tuple.of(
              type,
              status
      )) { ar ->
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

  fun getExamsByIds(ids: String, action: (JsonArray) -> Unit) {
    var fields = "`id`,`type`,`paper_id`,`time`,`title`,`status`,`begin_at`";
    var sql = "SELECT $fields FROM `exam` WHERE id IN($ids)"

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

  fun updateExamById(id: Int, exam: JsonObject, action: (Boolean) -> Unit) {
    val fields = ("type = ?, "
            + "paper_id = ?, "
            + "time = ?, "
            + "title = ?, "
            + "status = ?, "
            + "begin_at = ?")
    var sql = "UPDATE `exam` SET $fields WHERE `id` = ?"

    client?.let {
      it.preparedQuery(sql).execute(Tuple.of(
              exam.getInteger("type"),
              exam.getInteger("paper_id"),
              exam.getInteger("time"),
              exam.getString("title"),
              exam.getInteger("status"),
              exam.getString("begin_at"),
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

  fun updateActivityStatus(id: Int, exam: JsonObject, action: (Boolean) -> Unit) {
    val fields = (""
            + "status = ?, "
            + "fee_male = ?, "
            + "fee_female = ?"
            )
    var sql = "UPDATE `exam` SET $fields WHERE `id` = ?"

    client?.let {
      it.preparedQuery(sql).execute(Tuple.of(
              exam.getInteger("status"),
              exam.getInteger("fee_male"),
              exam.getInteger("fee_female"),
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
