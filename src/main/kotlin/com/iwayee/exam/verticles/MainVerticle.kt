package com.iwayee.exam.verticles

import com.iwayee.exam.api.system.ExamSystem
import com.iwayee.exam.api.system.PaperSystem
import com.iwayee.exam.api.system.UserSystem
import com.iwayee.exam.define.ErrCode
import com.iwayee.exam.hub.Hub
import com.iwayee.exam.hub.Some
import com.iwayee.exam.utils.TokenExpiredException
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import java.lang.IllegalArgumentException

class MainVerticle: AbstractVerticle() {
  private var router: Router? = null

  private fun json(ctx: RoutingContext, code: ErrCode) {
    ctx.json(JsonObject().put("code", code.errorCode).put("msg", code.errorDesc))
  }

  private fun errAuth(ctx: RoutingContext) {
    var code = ErrCode.ERR_AUTH
    json(ctx, code)
  }

  private fun errArg(ctx: RoutingContext) {
    var code = ErrCode.ERR_PARAM
    json(ctx, code)
  }

  override fun start(startPromise: Promise<Void>) {
    Hub.vertx = vertx
    Hub.loadConfig {
      startServer()
    }
  }

  private fun runAction(ctx: RoutingContext, action: (Some) -> Unit, auth: Boolean = true) {
    try {
      var some = Some(ctx)
      if (auth) {
         some.checkToken()
      }
      action(some)
    } catch (e: IllegalArgumentException) {
      errArg(ctx)
    } catch (e: TokenExpiredException) {
      errAuth(ctx)
    }
  }

  private fun get(s: String, action: (Some) -> Unit, auth: Boolean = true) {
    router?.get(s)?.handler{ctx ->
      runAction(ctx, action, auth)
    }
  }

  private fun post(s: String, action: (Some) -> Unit, auth: Boolean = true) {
    router?.post(s)?.handler{ctx ->
      runAction(ctx, action, auth)
    }
  }

  private fun put(s: String, action: (Some) -> Unit, auth: Boolean = true) {
    router?.put(s)?.handler{ctx ->
      runAction(ctx, action, auth)
    }
  }

  private fun delete(s: String, action: (Some) -> Unit, auth: Boolean = true) {
    router?.delete(s)?.handler{ctx ->
      runAction(ctx, action, auth)
    }
  }

  private fun startServer() {
    router = Router.router(vertx)
    router?.route()?.handler(BodyHandler.create())

    // 用户
    get("/users/:uid", UserSystem::getUser);
    get("/users/your/groups", PaperSystem::getGroupsByUserId);
    get("/users/your/activities", ExamSystem::getActivitiesByUserId);

    post("/login", UserSystem::login, false);
    post("/login_wx", UserSystem::wxLogin, false);
    post("/register", UserSystem::register, false);
    post("/logout", UserSystem::logout);

    // 群组
    get("/groups/:gid", PaperSystem::getGroupById);
    get("/groups", PaperSystem::getGroups);
    get("/groups/:gid/pending", PaperSystem::getApplyList);
    get("/groups/:gid/activities", ExamSystem::getActivitiesByGroupId);

    post("/groups", PaperSystem::create);
    post("/groups/:gid/apply", PaperSystem::apply);
    post("/groups/:gid/approve", PaperSystem::approve);
    post("/groups/:gid/promote/:mid", PaperSystem::promote);
    post("/groups/:gid/transfer/:mid", PaperSystem::transfer);

    put("/groups/:gid", PaperSystem::updateGroup);

    // 活动
    get("/activities/:aid", ExamSystem::getActivityById);
    get("/activities", ExamSystem::getActivities);

    post("/activities", ExamSystem::create);
    post("/activities/:aid/end", ExamSystem::end);
    post("/activities/:aid/apply", ExamSystem::apply);
    post("/activities/:aid/cancel", ExamSystem::cancel);

    put("/activities/:aid", ExamSystem::update);

    Hub.config?.let {
      vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(it.port)
        .onSuccess{ server ->
          println("HTTP server started on port ${server.actualPort()}")
        }
    }
  }
}
