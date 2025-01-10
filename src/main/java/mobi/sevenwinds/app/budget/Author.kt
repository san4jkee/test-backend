package mobi.sevenwinds.app.budget

import java.time.LocalDateTime

data class Author(
    val id: Int? = null,
    val fullName: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
