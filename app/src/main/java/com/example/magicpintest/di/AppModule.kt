package com.example.magicpintest.di

import com.example.magicpintest.usecase.magicnotesinteractor.MagicNotesInteractor
import com.example.magicpintest.usecase.repository.MagicNotesDatabase
import com.example.magicpintest.view.detail.NotesDetailViewModel
import com.example.magicpintest.view.magicnotes.MagicNotesViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val app_module = module {
    viewModel { MagicNotesViewModel(get()) }
    viewModel{ NotesDetailViewModel(get()) }
}
val single_module = module {
    single { MagicNotesInteractor(MagicNotesDatabase.getDatabase(get())) }
}



val disposables = CompositeDisposable()

fun launch(job: () -> Disposable) {
    disposables.add(job())
}