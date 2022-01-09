package com.example.magicpintest.view.detail

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.magicpintest.di.launch
import com.example.magicpintest.usecase.magicnotesinteractor.MagicNotesInteractor
import com.example.magicpintest.usecase.repository.NoteItem
import com.example.magicpintest.utils.SingleLiveEvent
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.FileOutputStream


class NotesDetailViewModel(private val magicNotesInteractor: MagicNotesInteractor) : ViewModel() {

    val feedbackSubmitted: LiveData<Unit> get() = _feedbackSubmitted
    private val _feedbackSubmitted: SingleLiveEvent<Unit> = SingleLiveEvent()

    val fileStream: LiveData<FileOutputStream> get() = _fileStream
    private val _fileStream: SingleLiveEvent<FileOutputStream> = SingleLiveEvent()


    fun onSendClicked(feedBackBox: String, feedBackBox1: String, uri: String?) {
        launch {
            magicNotesInteractor.insert(NoteItem(feedBackBox, feedBackBox1, uri = uri))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _feedbackSubmitted.postValue(Unit) }, ::onError)

        }
    }

    fun onFieldsUpdate(title: String, uri: String?, content: String, id: Int) {
        launch {
            magicNotesInteractor.insertStatus(id, title, content, uri)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _feedbackSubmitted.postValue(Unit) }, ::onError)

        }
    }

    fun onImagePresent(bitmap: Bitmap, outputStream: FileOutputStream) {
        launch {
            Maybe.just(bitmap)
                .subscribeOn(Schedulers.io())
                .map { it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) }
                .subscribe({ this._fileStream.postValue(outputStream) }, ::onError)
        }
    }


    private fun onError(throwable: Throwable) {
        Log.i("throwable", "" + throwable.localizedMessage)
    }
}
