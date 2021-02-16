package com.iwayee.exam.cache

import com.iwayee.exam.api.comp.Paper
import com.iwayee.exam.dao.mysql.PaperDao
import io.vertx.core.json.JsonObject

object PaperCache : BaseCache() {
  private var papers = mutableMapOf<Int, Paper>()

  private fun cache(paper: Paper) {
    paper?.let {
      papers[it.id] = it
    }
  }

  fun syncToDB(id: Int, action: (Boolean) -> Unit) {
    when {
      papers.containsKey(id) -> {
        var paper = papers[id]
        PaperDao.updatePaperById(id, JsonObject.mapFrom(paper)) {
          action(it)
        }
      }
      else -> action(false)
    }
  }
}
