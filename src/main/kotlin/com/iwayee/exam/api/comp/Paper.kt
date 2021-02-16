package com.iwayee.exam.api.comp

data class Paper(
        var id: Int = 0,
        var time: Int = 60, // 试卷持续时间
        var title: String = "",
        var config: String = ""
) {
  //
}
