package com.iwayee.exam.api.comp

import com.iwayee.exam.define.SexType
import io.vertx.core.json.JsonObject

data class Paper(
        var id: Int = 0,
        var sex: Int = SexType.MALE.ordinal,
        var wx_nick: String = "",
        var nick: String = ""
) {
  fun fromUser(user: User) {
    id = user.id
    sex = user.sex
    wx_nick = user.wx_nick
    nick = user.nick
  }

  fun toJson(): JsonObject {
    var jo = JsonObject()
    jo.put("id", id)
            .put("wxNick", wx_nick)
            .put("nick", nick)
            .put("sex", sex)
    return jo
  }
}
