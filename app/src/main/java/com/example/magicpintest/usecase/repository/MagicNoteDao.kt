package com.example.magicpintest.usecase.repository

import androidx.room.*
import com.example.magicpintest.model.MagicNote
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface MagicNoteDao {

    @Query("SELECT * FROM magic_notes")
    fun getAlphabetizedWords(): Observable<List<MagicNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(name: NoteItem): Completable

    @Query("UPDATE magic_notes SET title = :title, content = :content, uri= :uri WHERE id =:id")
    fun insertStatus(id: Int, title: String, content: String,uri: String?): Completable
}