package link.v01d.kosync.routes

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import link.v01d.kosync.classes.Error
import link.v01d.kosync.classes.User
import link.v01d.kosync.classes.UserAuth
import link.v01d.kosync.dao.UserDao

fun Route.userRoutes() {
//    val userService = UserDao()
    val gson = Gson()

    // Create user
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
        call.respond(HttpStatusCode.BadRequest, Error.Code.error_invalid_fields)
    }

    get("/users/auth") {
        val headers = call.request.headers
        val username = headers["x-auth-user"] ?: ""
        val password = headers["x-auth-key"] ?: ""
        val userAuth = UserAuth(username, password)
        val user = UserDao.auth(userAuth)
        if (user is User) {
            call.respond(HttpStatusCode.OK, gson.toJson(user))
        }
        call.respond(HttpStatusCode.BadRequest, "principal.name [$username|$password]")
    }

    // Read user
    get("/users/{username}") {
        val id = call.parameters["username"] ?: throw IllegalArgumentException("Invalid username")
        val user = UserDao.read(id)
        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
    // Update user
    put("/users/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        val user = call.receive<User>()
        UserDao.update(id, user)
        call.respond(HttpStatusCode.OK)
    }
    // Delete user
    delete("/users/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        UserDao.delete(id)
        call.respond(HttpStatusCode.OK)
    }
}
