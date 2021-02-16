package com.iwayee.exam.cache

import com.iwayee.exam.api.comp.Exam
import com.iwayee.exam.dao.mysql.ExamDao
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

object ExamCache : BaseCache() {
  private var activities = mutableMapOf<Int, Exam>()

  private fun cache(exam: Exam) {
    exam?.let {
      activities[it.id] = it
    }
  }

  fun create(jo: JsonObject, uid: Int, action: (Long) -> Unit) {
    ExamDao.create(jo) { newId ->
      when {
        newId <= 0L -> action(newId)
        else -> {
          var activity = jo.mapTo(Exam::class.java)
          activity.id = newId.toInt()
          cache(activity)
          action(newId)
        }
      }
    }
  }

  fun getActivityById(id: Int, action: (Exam?) -> Unit) {
    if (activities.containsKey(id)) {
      println("获取活动数据（缓存）: $id")
      action(activities[id])
    } else {
      println("获取活动数据（DB)：$id")
      ExamDao.getExamById(id) {
        it?.let {
          var activity = it.mapTo(Exam::class.java)
          cache(activity)
          action(activity)
        }?: action(null)
      }
    }
  }

  fun getActivitiesByIds(ids: List<Int>, action: (JsonArray) -> Unit) {
    var jr = JsonArray()
    var idsForDB = mutableListOf<Int>()
    if (ids.isEmpty()) {
      action(jr)
      return
    }

    for (id in ids) {
      when {
        jr.contains(id) -> continue
        activities.containsKey(id) -> {
          activities[id]?.let {
            jr.add(it)
          }
        }
        else -> idsForDB.add(id)
      }
    }

    println("获取活动数据（缓存）：${jr.encode()}")
    when {
      idsForDB.isEmpty() -> action(jr)
      else -> {
        var idStr = joiner.join(idsForDB)
        println("获取活动数据（DB）：$idStr")
        ExamDao.getExamsByIds(idStr) {
          it?.forEach { entry ->
            var jo = entry as JsonObject
            var activity = jo.mapTo(Exam::class.java)
            cache(activity)
            jr.add(activity)
          }
          action(jr)
        }
      }
    }
  }

  fun getActivitiesByType(type: Int, status: Int, page: Int, num: Int, action: (JsonArray) -> Unit) {
    // 缓存60秒
    ExamDao.getExamsByType(type, status, page, num) {
      var jr = JsonArray()
      when {
        it.isEmpty -> action(jr)
        else -> {
          it.forEach { v ->
            var activity = (v as JsonObject).mapTo(Exam::class.java)
            cache(activity)
            jr.add(activity)
          }
          action(jr)
        }
      }
    }
  }

  fun syncToDB(id: Int, action: (Boolean) -> Unit) {
    when {
      activities.containsKey(id) -> {
        var activity = activities[id]
        ExamDao.updateExamById(id, JsonObject.mapFrom(activity)) {
          action(it)
        }
      }
      else -> action(false)
    }
  }
}
