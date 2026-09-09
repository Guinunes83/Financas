import re

with open('app/src/main/java/com/example/data/FinancialEntryDao.kt', 'r') as f:
    content = f.read()

new_query = """    @Query("UPDATE financial_entries SET type = :type, name = :name, category = :category, amount = :amount, notes = :notes, planId = :planId WHERE recurrenceId = :recurrenceId AND dateMillis > :dateMillis")
    suspend fun updateFutureEntries(recurrenceId: String, dateMillis: Long, type: EntryType, name: String, category: String, amount: Double, notes: String, planId: Int?)
"""

content = content.replace(
    'suspend fun deleteFutureEntries(recurrenceId: String, dateMillis: Long)',
    'suspend fun deleteFutureEntries(recurrenceId: String, dateMillis: Long)\n\n' + new_query
)

with open('app/src/main/java/com/example/data/FinancialEntryDao.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/data/FinancialRepository.kt', 'r') as f:
    repo_content = f.read()

repo_methods = """
    suspend fun updateFutureEntries(entry: FinancialEntry) {
        if (entry.recurrenceId != null) {
            entryDao.updateFutureEntries(
                recurrenceId = entry.recurrenceId,
                dateMillis = entry.dateMillis,
                type = entry.type,
                name = entry.name,
                category = entry.category,
                amount = entry.amount,
                notes = entry.notes,
                planId = entry.planId
            )
        }
    }
"""

repo_content = repo_content.replace(
    'suspend fun deleteFutureEntries(recurrenceId: String, dateMillis: Long) = entryDao.deleteFutureEntries(recurrenceId, dateMillis)',
    'suspend fun deleteFutureEntries(recurrenceId: String, dateMillis: Long) = entryDao.deleteFutureEntries(recurrenceId, dateMillis)\n' + repo_methods
)

with open('app/src/main/java/com/example/data/FinancialRepository.kt', 'w') as f:
    f.write(repo_content)
