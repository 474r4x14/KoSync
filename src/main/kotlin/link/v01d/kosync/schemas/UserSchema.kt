package link.v01d.kosync.schemas

import org.jetbrains.exposed.sql.Table

object UserSchema : Table("user") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", length = 50)
    val password = varchar("password", length = 50)

    override val primaryKey = PrimaryKey(id)
}