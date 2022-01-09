package com.example.magicpintest.usecase.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteItem::class], version = 3, exportSchema = false)
     abstract class MagicNotesDatabase : RoomDatabase() {

        abstract fun wordDao(): MagicNoteDao

        companion object {
            @Volatile
            private var INSTANCE: MagicNotesDatabase? = null

            fun getDatabase(context: Context): MagicNotesDatabase {

                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        MagicNotesDatabase::class.java,
                        "word_database"
                    )
                        .build()
                    INSTANCE = instance

                    instance
                }
            }
        }
    }
