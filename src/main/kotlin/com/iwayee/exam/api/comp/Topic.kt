package com.iwayee.exam.api.comp

import com.iwayee.exam.define.GroupPosition
import com.iwayee.exam.define.SexType

data class Topic(
        var id: Int = 0,
        var scores: Int = 0,
        var pos: Int = GroupPosition.POS_MEMBER.ordinal,
        var sex: Int = SexType.MALE.ordinal,
        var at: Long = 0L,
        var nick: String = "",
        var wx_nick: String = ""
) {
  fun fromUser(user: User) {
    sex = user.sex
    wx_nick = user.wx_nick
    nick = user.nick
  }
}
