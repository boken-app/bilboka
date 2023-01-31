package bilboka.core.book

class DuplicateBookEntryException(
    message: String = "Kan ikke opprette to identiske oppføringer etter hverandre."
) : BookEntryException(message)
