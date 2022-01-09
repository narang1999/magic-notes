package com.example.magicpintest.view.magicnotes

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.magicpintest.R
import com.example.magicpintest.databinding.MagicNoteItemBinding
import com.example.magicpintest.extention.inflateChild
import com.example.magicpintest.model.MagicNote
import com.example.magicpintest.usecase.repository.NoteItem

class MagicNotesAdapter(val result: (NoteItem) -> Unit) :
    ListAdapter<MagicNote, RecyclerView.ViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MagicNote>() {
            override fun areItemsTheSame(oldItem: MagicNote, newItem: MagicNote): Boolean =
                oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: MagicNote, newItem: MagicNote): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostViewHolder(MagicNoteItemBinding.bind(parent.inflateChild(R.layout.magic_note_item)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            holder.bind(getItem(position))
        }
    }

    private inner class PostViewHolder(val binding: MagicNoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: MagicNote) {
            binding.title.text = item.title
            binding.content.text = item.content
            binding.root.setOnClickListener { result(NoteItem(item.title,item.content,item.uri,item.id)) }
        }
    }
}