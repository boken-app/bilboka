package bilboka.messagebot.commands.common

import bilboka.messagebot.Conversation

internal abstract class ChatCommand {
    abstract fun isMatch(message: String): Boolean
    abstract fun validUser(regTypeID: String, senderID: String): Boolean
    abstract fun execute(conversation: Conversation, message: String)
}

internal abstract class ChatState

internal abstract class DataCollectingChatState<T> : ChatState() {
    abstract val collectedData: Map<T, QueryableDataItem>
    abstract fun complete(): DataCollectingChatState<T>?

    fun recordProvidedData(message: String, valueMapper: T.() -> Any?) {
        collectedData.filter { it.value.wasJustQueried }.toList().firstOrNull()?.apply {
            this.second.wasJustQueried = false
            if (message.saysUnknown() && this.second.mayBeUnknown) {
                this.second.isUnknown = true
            } else {
                this.second.content = this.first.valueMapper()
            }
        }
    }
}

data class QueryableDataItem(
    val query: String,
    var content: Any? = null,
    var isUnknown: Boolean = false,
    val mayBeUnknown: Boolean = false,
    var wasJustQueried: Boolean = false
) {
    fun isNotChecked(): Boolean {
        return content == null && !isUnknown
    }

    fun isMissing(): Boolean {
        return content == null
    }

    fun isPresent(): Boolean {
        return content != null
    }
}

internal fun <T> ChatCommand.askForNext(conversation: Conversation, stateInProgress: DataCollectingChatState<T>) {
    stateInProgress.collectedData.values.first { it.content == null && !it.isUnknown }.let {
        it.wasJustQueried = true
        conversation.claim(this, stateInProgress)
        conversation.sendReply(it.query)
    }
}

internal fun String.saysUnknown(): Boolean {
    return Regex(
        "(ukjent|nei|\\?|dunno|vet ikke)",
        RegexOption.IGNORE_CASE
    ).containsMatchIn(this)
}

internal fun String.saysYes(): Boolean {
    return lowercase() == "ja"
}
