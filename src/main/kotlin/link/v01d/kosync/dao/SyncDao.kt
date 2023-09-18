package link.v01d.kosync.dao

import kotlinx.coroutines.Dispatchers
import link.v01d.kosync.classes.Sync
import link.v01d.kosync.classes.SyncUpdated
import link.v01d.kosync.classes.User
import link.v01d.kosync.plugins.DB
import link.v01d.kosync.schemas.SyncSchema
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object SyncDao {

        init {
            transaction(DB.conn) {
                SchemaUtils.create(SyncSchema)
            }
        }

        suspend fun <T> dbQuery(block: suspend () -> T): T =
            newSuspendedTransaction(Dispatchers.IO) { block() }

        suspend fun read(document: String, user: User): Sync? {
            val syncData = dbQuery {
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
                dbQuery {
                    SyncSchema.replace {
                        it[SyncSchema.userId] = userId
                        it[SyncSchema.device] = syncData.device
                        it[SyncSchema.deviceId] = syncData.deviceId
                        it[SyncSchema.document] = syncData.document
                        it[SyncSchema.progress] = syncData.progress
                        it[SyncSchema.percentage] = syncData.percentage
                        it[SyncSchema.dateCreated] = syncData.dateCreated
                    }
                }
                return SyncUpdated(syncData.document, syncData.dateCreated.millis.toString())
            }
            return null
        }
    }
