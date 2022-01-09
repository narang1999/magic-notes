package com.example.magicpintest.view.magicnotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.Menu
import androidx.core.widget.doOnTextChanged
import com.example.magicpintest.R
import com.example.magicpintest.databinding.MagicNotesBinding
import com.example.magicpintest.view.detail.NotesDetail
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.magicpintest.extention.viewBinding
import com.example.magicpintest.usecase.repository.NoteItem
import com.example.magicpintest.utils.showDialog


class MagicNotes : AppCompatActivity() {
    private var mainAdapter: MagicNotesAdapter? = null
    private val binding by viewBinding(MagicNotesBinding::inflate)
    private val viewModel: MagicNotesViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mainAdapter = MagicNotesAdapter {
            NotesDetail(it).showDialog(supportFragmentManager)
        }
        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.post.layoutManager = staggeredGridLayoutManager
        binding.post.adapter = mainAdapter
        binding.editText.doOnTextChanged { text, _, _, _ ->
            viewModel.onSearchQueryChanged(text.toString())
        }
        subscribeUi()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.magic_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionAdd -> {
                NotesDetail(NoteItem("", "", null, 0)).showDialog(supportFragmentManager)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun subscribeUi() {
        viewModel.allNotes.observe(this) {
            binding.editText.setText("")
            mainAdapter?.submitList(it)
        }
        viewModel.searchResult.observe(this) {
            mainAdapter?.submitList(it)
        }
    }

    override fun onDestroy() {
        mainAdapter = null
        super.onDestroy()
    }
}
