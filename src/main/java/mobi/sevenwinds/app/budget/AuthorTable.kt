package mobi.sevenwinds.app.budget

import org.jetbrains.exposed.dao.id.IntIdTable

object AuthorTable : IntIdTable("Author") {
    val fullName = varchar("full_name", 255)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())
}
