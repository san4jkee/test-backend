package mobi.sevenwinds.app.budget

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import java.time.LocalDateTime

fun Route.authorRoutes() {
    route("/author") {
        post("/add") {
            val fullName = call.receive<Map<String, String>>()["fullName"] ?: throw IllegalArgumentException("Full name is required")
            val author = AuthorService.addAuthor(fullName)
            call.respond(author)
        }
    }
}

object AuthorService {
    private val authors = mutableListOf<Author>()

    fun addAuthor(fullName: String): Author {
        val newAuthor = Author(
            id = authors.size + 1,
            fullName = fullName,
            createdAt = LocalDateTime.now()
        )
        authors.add(newAuthor)
        return newAuthor
    }
}
