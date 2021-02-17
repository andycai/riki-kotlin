package com.iwayee.exam.api.comp

import io.vertx.core.json.JsonObject

data class UserData(
        var id: Int = 0,
        var uid: Int = 0,
        var level: Int = 0,
        var scores: Int = 0,
        var topic_total: Int = 0,
        var topic_wrong: Int = 0,
        var practice: JsonObject = JsonObject(),
        var test: JsonObject = JsonObject(),
        var exam: JsonObject = JsonObject()
) {
  //
}
