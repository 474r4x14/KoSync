package link.v01d.kosync.classes

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.joda.time.DateTime

@Serializable
data class Sync(
    val document:String,
    val progress:String,
    val percentage:Double,
    val device:String,
    val deviceId:Int,
    @Contextual
    var dateCreated:DateTime
)

@Serializable
data class SyncUpdated(
    val document: String,
    val timestamp: String,
)