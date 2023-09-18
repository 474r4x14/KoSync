package link.v01d.kosync.dao

import kotlinx.coroutines.Dispatchers
import link.v01d.kosync.classes.User
import link.v01d.kosync.classes.UserAuth
import link.v01d.kosync.plugins.DB
import link.v01d.kosync.schemas.UserSchema
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object UserDao {

        init {
            transaction(DB.conn) {
                SchemaUtils.create(UserSchema)
            }
        }

        suspend fun <T> dbQuery(block: suspend () -> T): T =
            newSuspendedTransaction(Dispatchers.IO) { block() }

        suspend fun create(userAuth: UserAuth): User  {
            val insertId = dbQuery {
                UserSchema.insert {
                    it[username] = userAuth.username
                    it[password] = userAuth.password
                }[UserSchema.id]
            }
            val user = User(userAuth.username)
            user.id = insertId
            return user
        }

        suspend fun auth(userAuth: UserAuth): User?  {
            val userRow = dbQuery {
                UserSchema.select {
                    (UserSchema.username eq userAuth.username) and
                    (UserSchema.password eq userAuth.password)
                }.singleOrNull()
            }
            if (userRow is ResultRow) {
                val user = User(userRow[UserSchema.username])
                user.id = userRow[UserSchema.id]
                return user
            }
            return null
        }

        suspend fun read(username: String): User? {
            return dbQuery {
                UserSchema.select { UserSchema.username eq username }
                    .map { User(it[UserSchema.username]) }
                    .singleOrNull()
            }
        }

        suspend fun update(id: Int, user: User) {
            dbQuery {
                UserSchema.update({ UserSchema.id eq id }) {
                    it[username] = user.username
//                it[age] = user.age
                }
            }
        }

        suspend fun delete(id: Int) {
            dbQuery {
                UserSchema.deleteWhere { UserSchema.id.eq(id) }
            }
        }
    }
