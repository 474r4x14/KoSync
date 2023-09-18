package link.v01d.kosync.routes

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import link.v01d.kosync.classes.Sync
import link.v01d.kosync.classes.User
import link.v01d.kosync.classes.UserAuth
import link.v01d.kosync.dao.SyncDao
import link.v01d.kosync.dao.UserDao
import org.joda.time.DateTime
import java.security.Timestamp
import java.util.Date

fun Route.syncRoutes() {
    val gson = Gson()
/*
    // Push progress
    post("/users/create") {
        val userAuth = call.receive<UserAuth>()
        if ((userAuth.username.isNotEmpty() && userAuth.password.isNotEmpty())) {
            // Make sure the username is unique
            val userCheck = UserDao.read(userAuth.username)
            if (userCheck !is User) {
                val user = UserDao.create(userAuth)
                call.respond(HttpStatusCode.Created, gson.toJson(user))
            }
        }
        call.respond(HttpStatusCode.BadRequest, "invalid fields")
    }
*/
    // Get the latest hash
    get("/syncs/progress") {
        val headers = call.request.headers
        val username = headers["x-auth-user"] ?: ""
        val password = headers["x-auth-key"] ?: ""
        val userAuth = UserAuth(username, password)
        val params = call.parameters
        val hash = params["hash"]
        val user = UserDao.auth(userAuth)
        if (user is User) {
            call.respond(HttpStatusCode.OK, gson.toJson(user))
        }
        call.respond(HttpStatusCode.BadRequest, "principal.name [$username|$password|$hash]")
    }

    // Get the latest hash
    get("/syncs/progress/{hash}") {
        val headers = call.request.headers
        val username = headers["x-auth-user"] ?: ""
        val password = headers["x-auth-key"] ?: ""
        val userAuth = UserAuth(username, password)
//        val syncData = call.receive<Sync>()
//        val syncData = call.receiveParameters()
//        val params = call.parameters

        var hash = ""
        if (call.parameters.contains("hash")) {
            hash = call.parameters["hash"]!!
        }
        val user = UserDao.auth(userAuth)
        if (user is User) {
            val syncData = SyncDao.read(hash, user)
            if (syncData is Sync) {
                call.respond(HttpStatusCode.OK, gson.toJson(syncData))
            }
        }
        call.respond(HttpStatusCode.BadRequest, "principal.name [$username|$password|$hash]")
    }

    put ("/syncs/progress") {
        val headers = call.request.headers
        val username = headers["x-auth-user"] ?: ""
        val password = headers["x-auth-key"] ?: ""
        val userAuth = UserAuth(username, password)
        val userData = UserDao.auth(userAuth)
        if (userData is User) {
            val syncData = call.receive<Sync>()
            syncData.dateCreated = DateTime.now()
            val syncUpdated = SyncDao.update(syncData, userData.id)
            call.respond(HttpStatusCode.OK, gson.toJson(syncUpdated))

        }

        call.respond(HttpStatusCode.BadRequest, "principal.name [$username|$password]")
    }
}
