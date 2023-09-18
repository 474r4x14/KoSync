package link.v01d.kosync.dao

import kotlinx.coroutines.Dispatchers
import link.v01d.kosync.classes.User
import link.v01d.kosync.plugins.DB
import link.v01d.kosync.schemas.UserSchema
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class UserDao {

        init {
            transaction(DB.conn) {
                SchemaUtils.create(UserSchema)
            }
        }

        suspend fun <T> dbQuery(block: suspend () -> T): T =
            newSuspendedTransaction(Dispatchers.IO) { block() }

        suspend fun create(user: User): Int = dbQuery {
            UserSchema.insert {
                it[name] = user.name
//            it[age] = user.age
            }[UserSchema.id]
        }

        suspend fun read(id: Int): User? {
            return dbQuery {
                UserSchema.select { UserSchema.id eq id }
                    .map { User(it[UserSchema.name]) }
                    .singleOrNull()
            }
        }

        suspend fun update(id: Int, user: User) {
            dbQuery {
                UserSchema.update({ UserSchema.id eq id }) {
                    it[name] = user.name
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
