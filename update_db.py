import os

# 1. Update FinancialEntry.kt
with open('app/src/main/java/com/example/data/FinancialEntry.kt', 'r') as f:
    content = f.read()

content = content.replace('INCOME, EXPENSE', 'INCOME, EXPENSE, PLAN')
if 'val planId: Int? = null' not in content:
    content = content.replace('val recurrenceId: String? = null', 'val recurrenceId: String? = null,\n    val planId: Int? = null')

with open('app/src/main/java/com/example/data/FinancialEntry.kt', 'w') as f:
    f.write(content)

# 2. Create FinancialPlan.kt
plan_kt = """package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_plans")
data class FinancialPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val targetDateMillis: Long
)
"""
with open('app/src/main/java/com/example/data/FinancialPlan.kt', 'w') as f:
    f.write(plan_kt)

# 3. Create PlanDao.kt
dao_kt = """package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Query("SELECT * FROM financial_plans ORDER BY targetDateMillis ASC")
    fun getAllPlans(): Flow<List<FinancialPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: FinancialPlan)

    @Update
    suspend fun updatePlan(plan: FinancialPlan)

    @Delete
    suspend fun deletePlan(plan: FinancialPlan)
}
"""
with open('app/src/main/java/com/example/data/PlanDao.kt', 'w') as f:
    f.write(dao_kt)

# 4. Update AppDatabase.kt
with open('app/src/main/java/com/example/data/AppDatabase.kt', 'r') as f:
    db_content = f.read()

if 'FinancialPlan::class' not in db_content:
    db_content = db_content.replace('FinancialCategory::class]', 'FinancialCategory::class, FinancialPlan::class]')
    db_content = db_content.replace('version = 4', 'version = 5')
    db_content = db_content.replace('abstract fun categoryDao(): CategoryDao', 'abstract fun categoryDao(): CategoryDao\n    abstract fun planDao(): PlanDao')
    
    migration_4_5 = """
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `financial_plans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `targetAmount` REAL NOT NULL, `targetDateMillis` INTEGER NOT NULL)")
                db.execSQL("ALTER TABLE financial_entries ADD COLUMN planId INTEGER")
            }
        }"""
    
    db_content = db_content.replace('private val MIGRATION_3_4', migration_4_5 + '\n        private val MIGRATION_3_4')
    db_content = db_content.replace('.addMigrations(MIGRATION_3_4)', '.addMigrations(MIGRATION_3_4, MIGRATION_4_5)')

with open('app/src/main/java/com/example/data/AppDatabase.kt', 'w') as f:
    f.write(db_content)

print("Database updated!")
