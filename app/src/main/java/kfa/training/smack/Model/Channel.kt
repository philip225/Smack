package kfa.training.smack.Model

// Odd one here, the lint reports 'description' as unused, yet it is used!
// Suppressed lint.
class Channel(val name: String, @Suppress("unused") val description: String, val id:String) {
    override fun toString(): String {
        return "#$name"
    }
}