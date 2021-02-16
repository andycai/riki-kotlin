package com.iwayee.exam.api.comp

import com.iwayee.exam.define.ExamStatus
import com.iwayee.exam.define.ExamType

data class Exam(
        var id: Int = 0,
        var type: Int = ExamType.PRACTICE.ordinal, // 考试类型：1:训练;2:模拟考试;3:正式考试
        var paper_id: Int = 1, // 试卷ID
        var status: Int = ExamStatus.WAITING.ordinal, // 活动状态:1进行中,2正常结算完成,3手动终止
        var time: Int = 60, // 考试持续时间
        var title: String = "",
        var begin_at: String = ""
) {
  //
}
