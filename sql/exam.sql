--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`  varchar(128)        NOT NULL COMMENT '用户名',
    `password`  varchar(128)        NOT NULL DEFAULT '123456' COMMENT '密码',
    `token`     varchar(128)        NOT NULL COMMENT 'token',
    `wx_token`  varchar(128)        NOT NULL COMMENT '微信session_key',
    `wx_nick`   varchar(256)        NOT NULL COMMENT '微信昵称',
    `nick`      varchar(256)        NOT NULL COMMENT '昵称',
    `sex`       tinyint(2)          NOT NULL DEFAULT 1 COMMENT '性别:1男,2女',
    `phone`     varchar(11)                  DEFAULT NULL COMMENT '手机号码',
    `email`     varchar(128)                 DEFAULT NULL COMMENT '邮箱',
    `ip`        varchar(128)                 DEFAULT 0 COMMENT 'ip地址',
    `create_at` datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_at` datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_at` datetime                     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `username_uindex` (`username`) USING BTREE,
    KEY `token_index` (`token`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户表';

--
-- Table structure for table `data`
--

DROP TABLE IF EXISTS `data`;
CREATE TABLE `data`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '数据ID',
    `uid`         bigint(20) unsigned NOT NULL COMMENT '用户ID',
    `level`       int(11) unsigned    NOT NULL DEFAULT 0 COMMENT '等级',
    `scores`      int(11) unsigned    NOT NULL DEFAULT 0 COMMENT '积分',
    `topic_total` int(11) unsigned    NOT NULL DEFAULT 0 COMMENT '训练做题总数',
    `topic_wrong` int(11) unsigned    NOT NULL DEFAULT 0 COMMENT '训练错题总数',
    `practice`    text COMMENT '训练',
    `test`        text COMMENT '模拟考试',
    `exam`        text COMMENT '正式考试',
    `create_at`   datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_at`   datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_at`   datetime                     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uid_uindex` (`uid`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户数据表';

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
CREATE TABLE `subject`
(
    `id`        int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '科目ID',
    `title`     varchar(128)     NOT NULL COMMENT '标题',
    `config`    varchar(128)     NOT NULL COMMENT '配置',
    `create_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='科目表';

--
-- Table structure for table `paper`
--

DROP TABLE IF EXISTS `paper`;
CREATE TABLE `paper`
(
    `id`        int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
    `title`     varchar(128)     NOT NULL COMMENT '试卷标题',
    `time`      int(11) unsigned NOT NULL DEFAULT 60 COMMENT '考试持续时间，单位：分钟',
    `topics`    text COMMENT '试卷题目列表',
    `config`    text COMMENT '试卷配置，包括各种类型题目的数量和分数',
    `create_at` datetime                  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_at` datetime                  DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_at` datetime                  DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='试卷表';

--
-- Table structure for table `topic`
--

DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic`
(
    `id`         int(11) unsigned    NOT NULL AUTO_INCREMENT COMMENT '题目ID',
    `type`       tinyint(2) unsigned NOT NULL DEFAULT 1 COMMENT '题目类型：1:单选题;2:多选题;3:判断题',
    `subject_id` tinyint(2) unsigned NOT NULL DEFAULT 1 COMMENT '科目ID',
    `weight`     tinyint(2) unsigned NOT NULL DEFAULT 1 COMMENT '权重',
    `title`      varchar(128)        NOT NULL COMMENT '标题',
    `items`      text COMMENT '题目可选答案',
    `answer`     varchar(64) COMMENT '答案',
    `create_at`  datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_at`  datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_at`  datetime                     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    KEY `type_index` (`type`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='题目表';

--
-- Table structure for table `exam`
--

DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`
(
    `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '考试ID',
    `type`      tinyint(2) unsigned NOT NULL DEFAULT 1 COMMENT '考试类型：1:训练;2:模拟考试;3:正式考试',
    `paper_id`  int(11) unsigned    NOT NULL DEFAULT 0 COMMENT '试卷ID',
    `time`      int(11) unsigned    NOT NULL DEFAULT 60 COMMENT '考试持续时间',
    `title`     varchar(256)        NOT NULL COMMENT '考试标题',
    `status`    tinyint(2) unsigned NOT NULL DEFAULT 1 COMMENT '考试状态:1未开始,2进行中,3已结束',
    `begin_at`  datetime            NOT NULL COMMENT '考试开始时间',
    `create_at` datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_at` datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_at` datetime                     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    KEY `type_status_index` (`type`, `status`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='考试表';
