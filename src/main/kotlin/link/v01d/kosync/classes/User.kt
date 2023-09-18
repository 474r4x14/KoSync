package link.v01d.kosync.classes

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String
) {
    var id:Int = 0
}
@Serializable
data class UserAuth(
    val username: String,
    val password:String
)
