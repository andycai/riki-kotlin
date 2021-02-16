package com.iwayee.exam.api.comp

import com.iwayee.exam.define.SexType

data class Session(
        var uid: Int = 0,
        var sex: Int = SexType.MALE.ordinal,
        var at: Long = 0L,
        var token: String = ""
)
