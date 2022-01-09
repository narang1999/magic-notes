package com.example.magicpintest.view.detail

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.magicpintest.BuildConfig
import com.example.magicpintest.R
import com.example.magicpintest.databinding.NotesDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import android.graphics.drawable.BitmapDrawable
import android.view.*
import com.example.magicpintest.extention.viewBinding
import com.example.magicpintest.usecase.repository.NoteItem
import java.io.FileOutputStream


class NotesDetail(
    val noteItem: NoteItem
) : BottomSheetDialogFragment() {
    private val binding: NotesDialogBinding by viewBinding(NotesDialogBinding::bind)
    private val viewModel: NotesDetailViewModel by inject()
    private var photoUri: Uri? = null
    private val imageFile by lazy { File.createTempFile("note_image", ".jpg", storageDir) }
    private val storageDir by lazy { File(requireContext().filesDir, "images") }
    lateinit var photoFile: File

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val resultDialogFragment = super.onCreateDialog(savedInstanceState)
        setFullHeight(resultDialogFragment)
        return resultDialogFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.notes_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        subscribeUi()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.magic_add, menu)

    }

    private fun subscribeUi() {
        viewModel.feedbackSubmitted.observe(viewLifecycleOwner) {
            dismiss()
        }
        viewModel.fileStream.observe(viewLifecycleOwner) {
            sendTextWithImage(it)
        }

    }

    private fun createIntentChooser() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(galleryIntent)
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                photoUri = (it.data?.data)
                binding.addImage.visibility = View.GONE
                binding.image.visibility = View.VISIBLE
                Glide.with(requireContext())
                    .load(photoUri)
                    .into(binding.image)
            }
        }

    private fun initViews() {
        binding.closeBtn.setOnClickListener {
            dismiss()
        }
        binding.titleEdit.setText(noteItem.title)
        binding.contentEdit.setText(noteItem.content)
        binding.addImage.setOnClickListener { createIntentChooser() }

        noteItem.uri?.let {
            binding.addImage.visibility = View.GONE
            binding.image.visibility = View.VISIBLE
            photoUri = Uri.parse(it)
            Glide.with(requireContext())
                .load(photoUri)
                .into(binding.image)
        }

        binding.share.setOnClickListener {
            if (!areFieldsEmpty()) {
                prepareDataForSharing()
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.error_empty_dialog_message,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        binding.sendBtn.setOnClickListener {
            val uri = if (photoUri == null) null
            else photoUri.toString()
            when {
                areFieldsEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.error_empty_dialog_message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                noteItem.id == 0 -> {
                    viewModel.onSendClicked(
                        binding.titleEdit.text.toString(),
                        binding.contentEdit.text.toString(),
                        uri
                    )
                }
                else -> {
                    viewModel.onFieldsUpdate(
                        id = noteItem.id,
                        uri = uri,
                        title = binding.titleEdit.text.toString(),
                        content = binding.contentEdit.text.toString()
                    )
                }
            }
        }
    }

    private fun areFieldsEmpty(): Boolean =
        binding.titleEdit.text.toString().isEmpty() && binding.contentEdit.text.toString()
            .isEmpty()

    private fun setFullHeight(dialogFragment: Dialog) {
        dialogFragment.setOnShowListener { dialog ->
            val params = binding.root.layoutParams as FrameLayout.LayoutParams
            params.height = Resources.getSystem().displayMetrics.heightPixels
            binding.root.layoutParams = params

            val bottomSheetDialog: BottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetLayout: FrameLayout? =
                bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetLayout!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun prepareDataForSharing() {
        if (binding.image.drawable != null) {
            val bm: Bitmap = (binding.image.drawable as BitmapDrawable).bitmap
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                Toast.makeText(requireContext(), R.string.technical_messgae, Toast.LENGTH_SHORT)
            }
            val outputStream = FileOutputStream(photoFile)
            viewModel.onImagePresent(bm, outputStream)
        } else {
            sendingTextOnly()
        }
    }

    private fun createImageFile(): File {
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }

        return imageFile
    }

    private fun sendTextWithImage(outputStream: FileOutputStream) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        photoUri =
            FileProvider.getUriForFile(
                requireContext(),
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                photoFile
            )
        outputStream.flush()
        outputStream.close()
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            binding.titleEdit.text.toString() + "\n " + binding.contentEdit.text.toString()
        )
        try {
            startActivity(Intent.createChooser(shareIntent, "Send items to..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "There is no one to accept this",
                Toast.LENGTH_SHORT
            )
                .show()
        }

    }

    private fun sendingTextOnly() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            binding.titleEdit.text.toString() + "\n " + binding.contentEdit.text.toString()
        )
        shareIntent.type = "text/html"
        try {
            startActivity(Intent.createChooser(shareIntent, "Send items to..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "There is no one to accept this",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    override fun dismiss() {
        super.dismiss()
        storageDir.deleteRecursively()
    }
}
