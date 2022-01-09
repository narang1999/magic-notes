package com.example.magicpintest.view.magicnotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import com.example.magicpintest.di.launch
import com.example.magicpintest.model.MagicNote
import com.example.magicpintest.usecase.magicnotesinteractor.MagicNotesInteractor
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers


class MagicNotesViewModel(
    private val magicNotesInteractor: MagicNotesInteractor
) : ViewModel() {

    val allNotes: LiveData<List<MagicNote>> get() = _allNotes

    private var _allNotes: MutableLiveData<List<MagicNote>> =
        MutableLiveData<List<MagicNote>>()
    val searchResult
        get() = magicNotesInteractor.searchResultsObservable
            .toFlowable(BackpressureStrategy.LATEST).toLiveData()


    init {
        launch {
            magicNotesInteractor.magicNotes
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_allNotes::postValue) //error handling is missing because post that I got covid
        }
    }

    fun onSearchQueryChanged(text: String) = magicNotesInteractor.searchForKeywords(text)

}
