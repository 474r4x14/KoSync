package link.v01d.kosync.classes

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.DateFormat

@Serializable
data class Sync(
    val document:String,
    val progress:String,
    val percentage:Double,
    val device:String,
    val deviceId:Int,
    @Contextual
    var dateCreated:DateTime
) {
    /*
    @Contextual
    var dateCreatedRaw:DateTime = DateTime()
        set(value) {
            field = value
            val fmt = DateTimeFormat.forPattern("yyyy-MM-dd")
            dateCreated = fmt.print(value)
        }
    var dateCreated:String = ""
    */
}

@Serializable
data class SyncUpdated(
    val document: String,
    val timestamp: String,
)