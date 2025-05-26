package com.example.unigym2.Fragments.Chat.Recyclerviews

class Message {
    var message: String? = null
    var senderId: String? = null
    var receiverId: String? = null
    var timestamp: Long? = null

    constructor() {}

    constructor(message: String?, senderId: String?, receiverId: String?, timestamp: Long?) {
        this.message = message
        this.senderId = senderId
        this.receiverId = receiverId
        this.timestamp = timestamp
    }
}
