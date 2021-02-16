package com.iwayee.exam.api.comp

import com.iwayee.exam.define.SexType
import io.vertx.core.json.JsonArray

data class User(
        var id: Int = 0,
        var sex: Int = SexType.MALE.ordinal,
        var scores: Int = 0,
        var username: String = "",
        var password: String = "",
        var nick: String = "",
        var wx_nick: String = "",
        var token: String = "",
        var wx_token: String = "",
        var ip: String = "",
        var phone: String = "",
        var email: String = "",
        var create_at: String = "",
) {
}
