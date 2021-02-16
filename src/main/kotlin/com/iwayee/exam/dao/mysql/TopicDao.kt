package com.iwayee.exam.dao.mysql

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.mysqlclient.MySQLClient
import io.vertx.sqlclient.Tuple

object TopicDao : MyDao() {
  fun create(topic: JsonObject, action: (Long) -> Unit) {
    var fields = "`type`,`subject_id`,`weight`,`title`,`items`,`answer`";
    var sql = "INSERT INTO `topic` ($fields) VALUES (?,?,?,?,?,?)"

    client?.let {
      it.preparedQuery(sql).execute(Tuple.of(
              topic.getInteger("type"),
              topic.getInteger("subject_id"),
              topic.getInteger("weight"),
              topic.getString("title"),
              topic.getString("items"),
              topic.getString("answer")
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

  fun getTopicById(id: Int, action: (JsonObject?) -> Unit) {
    var fields = "`id`,`type`,`subject_id`,`weight`,`title`,`items`,`answer`";
    var sql = "SELECT $fields FROM `topic` WHERE id = ?"

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

  fun getTopics(page: Int, num: Int, action: (JsonArray) -> Unit) {
    var fields = "`id`,`type`,`subject_id`,`weight`,`title`,`items`,`answer`";
    var sql = "SELECT $fields FROM `topic` ORDER BY id DESC LIMIT ${(page - 1) * num},$num"

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

  fun getTopicsByIds(ids: String, action: (JsonArray) -> Unit) {
    var fields = "`id`,`type`,`subject_id`,`weight`,`title`,`items`,`answer`";
    var sql = "SELECT $fields FROM `subject` WHERE id IN($ids)"

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

  fun updateTopicById(id: Int, topic: JsonObject, action: (Boolean) -> Unit) {
    val fields = ("type = ?, "
            + "subject_id = ?, "
            + "weight = ?, "
            + "title = ?, "
            + "items = ?, "
            + "answer = ?")
    var sql = "UPDATE `subject` SET $fields WHERE id = ?"

    client?.let {
      it.preparedQuery(sql).execute(Tuple.of(
              topic.getInteger("type"),
              topic.getInteger("subject_id"),
              topic.getInteger("weight"),
              topic.getString("title"),
              topic.getString("items"),
              topic.getString("answer"),
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
