package com.example.magicpintest.usecase.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "magic_notes")
class NoteItem(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "uri") val uri: String?= null,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

