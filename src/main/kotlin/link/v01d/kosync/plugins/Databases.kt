package link.v01d.kosync.plugins

import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DB {
    lateinit var conn:Database
    suspend fun <T> query(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

}

fun Application.configureDatabases() {
    val hostname = environment.config.property("db.host").getString()
    val username = environment.config.property("db.username").getString()
    val password = environment.config.property("db.password").getString()
    val database = environment.config.property("db.database").getString()


    DB.conn = Database.connect(
        url = "jdbc:mysql://$hostname:3306/$database",
        user = username,
        driver = "com.mysql.cj.jdbc.Driver",
        password = password
    )
}
