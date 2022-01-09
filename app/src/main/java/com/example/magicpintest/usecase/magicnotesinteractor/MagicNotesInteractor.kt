package com.example.magicpintest.usecase.magicnotesinteractor


import com.example.magicpintest.di.launch
import com.example.magicpintest.model.MagicNote
import com.example.magicpintest.usecase.repository.MagicNotesDatabase
import com.example.magicpintest.usecase.repository.NoteItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


class MagicNotesInteractor(private val magicNotesDatabase: MagicNotesDatabase) {
    private val _searchResults: PublishSubject<String> = PublishSubject.create()
    val searchResultsObservable: Observable<List<MagicNote>>
        get() = _searchResults.debounce(300L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .map(::performSearch)
            .observeOn(AndroidSchedulers.mainThread())
    private val magicNotesSubject: BehaviorSubject<List<MagicNote>> = BehaviorSubject.create()

    val magicNotes: Observable<List<MagicNote>> get() = magicNotesSubject.hide()

    init {
        getAllNotes()
    }

    var noteItemList = mutableListOf<MagicNote>()
    private fun performSearch(queryString: String): List<MagicNote> {
        var noteItems = mutableListOf<MagicNote>()
        val query=queryString.lowercase()
        noteItemList.forEach {
            if (it.title.lowercase().contains(query) || it.content.lowercase().contains(query)) {
                noteItems.add(it)
            }
        }
        return noteItems

    }

    private fun getAllNotes() {
        launch {
            magicNotesDatabase.wordDao()
                .getAlphabetizedWords()
                .subscribeOn(Schedulers.io()).subscribe {
                    noteItemList = it as MutableList<MagicNote>
                    magicNotesSubject.onNext(it)
                }
        }

    }

    fun searchForKeywords(query: String) {
        _searchResults.onNext(query)
    }

    fun insert(note: NoteItem): Completable = magicNotesDatabase.wordDao().insert(note)


   fun insertStatus(id: Int, title: String, content: String, uri: String?): Completable =
        magicNotesDatabase.wordDao().insertStatus(id, title, content, uri)
            .subscribeOn(Schedulers.io())

}