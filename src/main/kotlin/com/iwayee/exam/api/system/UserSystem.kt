package com.iwayee.exam.api.system

import com.iwayee.exam.api.comp.User
import com.iwayee.exam.cache.UserCache
import com.iwayee.exam.define.ErrCode
import com.iwayee.exam.hub.Some
import com.iwayee.exam.utils.Encrypt
import io.vertx.core.json.JsonObject

object UserSystem {
  private fun user2Json(user: User): JsonObject {
    var jo = JsonObject.mapFrom(user)
    jo.remove("password")
    return jo
  }

  fun login(some: Some) {
    var name = some.jsonStr("username")
    var wxNick = some.jsonStr("wx_nick")
    var sex = some.jsonUInt("sex")

    UserCache.getUserByName(name) { it ->
      if (it == null) {
        var ip = some.getIP()
        var jo = JsonObject()
        jo.put("username", name)
                .put("password", Encrypt.md5("123456"))
                .put("token", Encrypt.md5(name))
                .put("wx_token", Encrypt.md5(name))
                .put("wx_nick", wxNick)
                .put("nick", "")
                .put("sex", sex)
                .put("phone", "")
                .put("email", "")
                .put("ip", ip)
                .put("activities", "[]")
                .put("groups", "[]")
        UserCache.create(jo) { newId ->
          when (newId) {
            0L -> some.err(ErrCode.ERR_OP)
            else -> {
              var token = jo.getString("token")
              UserCache.cacheSession(token, newId.toInt(), sex)
              UserCache.getUserById(newId.toInt()) { user ->
                user?.let {
                  some.ok(user2Json(user))
                }?:let {
                  some.err(ErrCode.ERR_AUTH)
                }
              }
            }
          }
        }
      } else {
        UserCache.cacheSession(it.token, it.id, it.sex)
        some.ok(user2Json(it))
      }
    }
  }

  fun wxLogin(some: Some) {
    //
  }

  fun register(some: Some) {
    //
  }

  fun logout(some: Some) {
    UserCache.clearSession(some.token)
    some.succeed()
  }

  fun getUserByName(some: Some) {
    var name = some.getStr("username");

    UserCache.getUserByName(name) {
      it?.let {
        some.ok(user2Json(it))
      }?:let {
        some.err(ErrCode.ERR_DATA)
      }
    }
  }

  fun getUser(some: Some) {
    var uid = some.getUInt("uid");

    UserCache.getUserById(uid) {
      it?.let {
        some.ok(user2Json(it))
      }?:let {
        some.err(ErrCode.ERR_DATA)
      }
    }
  }
}
