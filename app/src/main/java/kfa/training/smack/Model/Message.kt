package kfa.training.smack.Model

// Again as we had with Channel, the lint reports channelId has never being used!
// Suppressed lint since channelId is used!
class Message(val message: String, val userName: String, @Suppress("unused") val channelId: String,
              val userAvatar: String, val userAvatarColour: String, val id: String,
              val timeStamp: String)