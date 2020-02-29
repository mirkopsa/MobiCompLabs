package com.mobicomp.labs

import androidx.room.*

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) var uId: Int?,
    @ColumnInfo(name = "time") var time: Long?,
    @ColumnInfo(name = "location") var location: String?,
    @ColumnInfo(name = "message") var message: String
)

@Dao
interface ReminderDao {
    @Transaction @Insert
    fun insert(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE uId = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM reminders")
    fun getReminders(): List<Reminder>
}