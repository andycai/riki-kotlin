package com.iwayee.exam.api.comp

import com.iwayee.exam.define.TopicType

data class Topic(
        var id: Int = 0,
        var type: Int = TopicType.SINGLE.ordinal,
        var subject_id: Int = 0,
        var weight: Long = 0L,
        var title: String = "",
        var items: String = "",
        var answer: String = ""
) {
  //
}
