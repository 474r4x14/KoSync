package link.v01d.kosync.dao

import link.v01d.kosync.classes.User
import link.v01d.kosync.classes.UserAuth
import link.v01d.kosync.plugins.DB
import link.v01d.kosync.schemas.UserSchema
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object UserDao {

        init {
            transaction(DB.conn) {
                SchemaUtils.create(UserSchema)
            }
        }


        suspend fun create(userAuth: UserAuth): User  {
            val insertId = DB.query {
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
            val userRow = DB.query {
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
            return DB.query {
                UserSchema.select { UserSchema.username eq username }
                    .map { User(it[UserSchema.username]) }
                    .singleOrNull()
            }
        }

        suspend fun update(id: Int, user: User) {
            DB.query {
                UserSchema.update({ UserSchema.id eq id }) {
                    it[username] = user.username
//                it[age] = user.age
                }
            }
        }

        suspend fun delete(id: Int) {
            DB.query {
                UserSchema.deleteWhere { UserSchema.id.eq(id) }
            }
        }
    }
