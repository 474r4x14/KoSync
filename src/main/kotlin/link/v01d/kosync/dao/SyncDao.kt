package link.v01d.kosync.dao

import link.v01d.kosync.classes.Sync
import link.v01d.kosync.classes.SyncUpdated
import link.v01d.kosync.classes.User
import link.v01d.kosync.plugins.DB
import link.v01d.kosync.schemas.SyncSchema
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object SyncDao {

        init {
            transaction(DB.conn) {
                SchemaUtils.create(SyncSchema)
            }
        }


        suspend fun read(document: String, user: User): Sync? {
            val syncData = DB.query {
                SyncSchema.select {
                    SyncSchema.document eq document
                    SyncSchema.userId eq user.id
                }
                    .orderBy(SyncSchema.dateCreated,SortOrder.DESC)
                    .limit(1)
                .singleOrNull()
            }
            if (syncData is ResultRow) {
                return Sync(
                    syncData[SyncSchema.document],
                    syncData[SyncSchema.progress],
                    syncData[SyncSchema.percentage],
                    syncData[SyncSchema.device],
                    syncData[SyncSchema.deviceId],
                    syncData[SyncSchema.dateCreated],
                )
            }
            return null
        }

        suspend fun update(syncData:Sync, userId: Int): SyncUpdated? {
            if (
                syncData.percentage > 0 &&
                syncData.progress.isNotEmpty() &&
                syncData.device.isNotEmpty()
            ) {
                DB.query {
                    SyncSchema.replace {
                        it[SyncSchema.userId] = userId
                        it[device] = syncData.device
                        it[deviceId] = syncData.deviceId
                        it[document] = syncData.document
                        it[progress] = syncData.progress
                        it[percentage] = syncData.percentage
                        it[dateCreated] = syncData.dateCreated
                    }
                }
                return SyncUpdated(syncData.document, syncData.dateCreated.millis.toString())
            }
            return null
        }
    }
