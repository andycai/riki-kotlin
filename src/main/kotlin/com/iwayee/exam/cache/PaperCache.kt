package com.iwayee.exam.cache

import com.iwayee.exam.api.comp.Subject
import com.iwayee.exam.dao.mysql.PaperDao
import com.iwayee.exam.define.GroupPosition
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.util.*

object PaperCache : BaseCache() {
  private var groups = mutableMapOf<Int, Subject>()

  private fun cache(subject: Subject) {
    subject?.let {
      groups[it.id] = it
    }
  }

  fun create(jo: JsonObject, uid: Int, action: (Long) -> Unit) {
    var group = jo.mapTo(Subject::class.java)
    var now = Date().time
    var member = JsonObject()
    member.put("id", uid)
            .put("scores", 0)
            .put("pos", GroupPosition.POS_OWNER.ordinal)
            .put("at", now)
    group.members = JsonArray().add(member)
    group.pending = JsonArray()
    group.activities = JsonArray()
    PaperDao.create(JsonObject.mapFrom(group)) {
      if (it > 0L) {
        group.id = it.toInt()
        cache(group)
      }
      action(it)
    }
  }

  fun getGroupById(id: Int, action: (Subject?) -> Unit) {
    if (groups.containsKey(id)) {
      println("获取群组数据（缓存）: $id")
      action(groups[id])
    } else {
      println("获取群组数据（DB)：$id")
      PaperDao.getPaperById(id) {
        it?.let {
          var group = it.mapTo(Subject::class.java)
          cache(group)
          action(group)
        }?: action(null)
      }
    }
  }

  fun getGroupsByIds(ids: List<Int>, action: (JsonArray) -> Unit) {
    var jr = JsonArray()
    var idsForDB = mutableListOf<Int>()
    if (ids.isEmpty()) {
      action(jr)
      return
    }

    for (id in ids) {
      when {
        jr.contains(id) -> continue
        groups.containsKey(id) -> {
          groups[id]?.let {
            jr.add(it.toJson())
          }
        }
        else -> idsForDB.add(id)
      }
    }

    println("获取群组数据（缓存）：${jr.encode()}")
    when {
      idsForDB.isEmpty() -> action(jr)
      else -> {
        var idStr = joiner.join(idsForDB)
        println("获取群组数据（DB）：$idStr")
        PaperDao.getPapersByIds(idStr) {
          it?.forEach { entry ->
            var jo = entry as JsonObject
            var group = jo.mapTo(Subject::class.java)
            cache(group)
            jr.add(group.toJson())
          }
          action(jr)
        }
      }
    }
  }

  fun getGroups(page: Int, num: Int, action: (JsonArray) -> Unit) {
    // 缓存60秒
    PaperDao.getPapers(page, num) {
      var jr = JsonArray()
      for (g in it) {
        var group = (g as JsonObject).mapTo(Subject::class.java)
        cache(group)
        groups[group.id] = group

        jr.add(group.toJson())
      }
      action(jr)
    }
  }

  fun syncToDB(id: Int, action: (Boolean) -> Unit) {
    when {
      groups.containsKey(id) -> {
        var group = groups[id]
        PaperDao.updatePaperById(id, JsonObject.mapFrom(group)) {
          action(it)
        }
      }
      else -> action(false)
    }
  }
}
