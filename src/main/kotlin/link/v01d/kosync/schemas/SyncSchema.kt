package link.v01d.kosync.schemas

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

object SyncSchema : Table("sync") {
//    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UserSchema.id)
    val document = varchar("document",500)
    val progress = varchar("progress",500)
    val percentage = double("percentage")
    val device = varchar("device",500)
    val deviceId = integer("device_id")
    val dateCreated = datetime("date_created")

    override val primaryKey = PrimaryKey(userId, document)
}