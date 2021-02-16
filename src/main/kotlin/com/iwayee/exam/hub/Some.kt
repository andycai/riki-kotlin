package com.iwayee.exam.hub

import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Strings
import com.iwayee.exam.cache.UserCache
import com.iwayee.exam.define.ErrCode
import com.iwayee.exam.utils.TokenExpiredException
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

class Some(ctx: RoutingContext) {
  var ctx: RoutingContext = ctx
  var request: HttpServerRequest? = null
    get() = ctx.request()
  var response: HttpServerResponse? = null
    get() = ctx.response()

  var userId: Int = 0
    get() {
      return UserCache.currentId(token)
    }
  var userSex: Int = 0
    get() {
      return UserCache.currentSex(token)
    }
  var token: String = ""
    get() {
      var param = getJson()
      var token = param.getString("token")
      checkArgument(!Strings.isNullOrEmpty(token))
      return token
    }

  fun getIP(): String {
    return ctx.request().remoteAddress().hostAddress()
  }

  fun checkToken() {
    if (UserCache.expired(token)) {
      throw TokenExpiredException("Login session has expired!")
    }
  }

  fun json(json: Any) {
    ctx.json(json)
  }

  fun getUInt(key: String): Int {
    var v = ctx.request().getParam(key).toInt()
    checkArgument(v > 0)
    return v
  }

  fun getInt(key: String): Int {
    var v = ctx.request().getParam(key).toInt()
    checkArgument(v >= 0)
    return v
  }

  fun getStr(key: String): String {
    var v = ctx.request().getParam(key)
    checkArgument(!Strings.isNullOrEmpty(v))
    return v
  }

  fun getJson(): JsonObject {
    var v = ctx.bodyAsJson;
    checkArgument(v != null)
    return v
  }

  fun jsonUInt(key: String): Int {
    var param = getJson()
    var v = param.getInteger(key)
    checkArgument(v > 0)
    return v
  }

  fun jsonInt(key: String): Int {
    var param = getJson()
    var v = param.getInteger(key)
    checkArgument(v >= 0)
    return v
  }

  fun jsonStr(key: String): String {
    var param = getJson()
    var v = param.getString(key)
    checkArgument(!Strings.isNullOrEmpty(v))
    return v
  }

  fun jsonBool(key: String): Boolean {
    var param = getJson()
    return param.getBoolean(key)
  }

  fun succeed() {
    err(ErrCode.SUCCESS)
  }

  fun ok(data: Any) {
    var code = ErrCode.SUCCESS
    ret(code.errorCode, data)
  }

  fun err(code: ErrCode) {
    msg(code.errorCode, code.errorDesc)
  }

  fun ret(code: Int, data: Any) {
    ctx.json(JsonObject().put("code", code).put("data", data))
  }

  fun msg(code: Int, msg: String) {
    ctx.json(JsonObject().put("code", code).put("msg", msg))
  }
}
