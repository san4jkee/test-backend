package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.authorId = body.authorId // Привязка к автору, если указано
            }

            val authorName = body.authorId?.let { AuthorTable.select { AuthorTable.id eq it }.singleOrNull()?.get(AuthorTable.fullName) }

            return@transaction entity.toResponse().copy(authorName = authorName)
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val query = BudgetTable
                .leftJoin(AuthorTable) // Присоединяем таблицу авторов
                .select { BudgetTable.year eq param.year }
                .apply {
                    // Фильтрация по ФИО автора, если указано
                    param.authorName?.let { authorName ->
                        andWhere { AuthorTable.fullName.lowerCase() like "%${authorName.lowercase()}%" }
                    }
                }
                .limit(param.limit, param.offset)

            val total = BudgetTable.select { BudgetTable.year eq param.year }.count()
            val data = BudgetEntity.wrapRows(query).map { 
                it.toResponse().copy(
                    authorName = it.authorId?.let { authorId ->
                        AuthorTable.select { AuthorTable.id eq authorId }
                            .singleOrNull()?.get(AuthorTable.fullName)
                    }
                )
            }

            val sumByType = data.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = data
            )
        }
    }
}
