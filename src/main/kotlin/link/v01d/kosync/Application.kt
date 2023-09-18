package link.v01d.kosync

import io.ktor.server.application.*
import link.v01d.kosync.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.jetty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting()
}
