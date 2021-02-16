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
