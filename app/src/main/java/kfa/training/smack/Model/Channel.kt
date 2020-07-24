package kfa.training.smack.Model

class Channel(val name: String, description: String, id:String) {
    override fun toString(): String {
        return "#$name"
    }
}