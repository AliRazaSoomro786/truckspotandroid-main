package com.truckspot.fragment.ui.reports

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import com.truckspot.R
import com.truckspot.databinding.FragmentReportsBinding
import com.truckspot.fragment.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@AndroidEntryPoint
class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.actionSendEmail.setOnClickListener {
            val email = binding.textureEmailAddress.text.toString()
            if (email.isEmpty() || !isValidEmail(email)) {
                makeText(activity, "valid email required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            sendEmailWithAttachment(email)

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendEmailWithAttachment(email: String) {
        val recipients = arrayOf(email)
        val subject = "Reports"
        val body = "Reports"

        // Get the file to attach (example: image stored in external storage)
//        val attachmentFile = File(getExternalFilesDir(null), "example_image.jpg")

        // Create the Intent with ACTION_SEND
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, "Please find the attached PDF report.")

        val recyclerView: RecyclerView = binding.layoutPrint.reportsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val layout = binding.layoutPrint.mainConstraintLayout

        apiScope.launch {
            val result = async { return@async homeViewModel.getCustomLogs(1, 50, 0) }.await()
            withContext(Dispatchers.Main) {
                if (result.isSuccessful) {
                    val logs = result.body()!!.results.userLogs
                    recyclerView.adapter = ReportsAdapterAdaptor(logs)

                    Handler().postDelayed({
                        generatePDF(layout)
                        val path = requireActivity().cacheDir.absolutePath.plus("/reports.pdf")

                        val fileUri: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "${requireContext().packageName}.fileprovider",
                            File(path)
                        )

                        Log.d(TAG, "sendEmailWithAttachment $fileUri")
                        intent.type = "application/pdf"
                        intent.putExtra(Intent.EXTRA_STREAM, fileUri)

                        if (intent.resolveActivity(requireActivity().packageManager) != null) {
                            startActivity(Intent.createChooser(intent, "Send Email"))
                        }
                    }, 2000)
                }
            }

        }
    }

/*
    fun exportSettings() {
        val chooser = StorageChooser.Builder().withActivity(activity)
            .withFragmentManager(requireActivity().fragmentManager)
            .withMemoryBar(true).setDialogTitle("Select folder for settings")
            .allowCustomPath(true).setType(StorageChooser.DIRECTORY_CHOOSER).build()
        chooser.show()
        chooser.setOnSelectListener { str: String ->
            Log.d(TAG, "exportSettings: $str")
        }
    }
*/

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        // Compress the bitmap to the stream in PNG format (can also use JPEG if needed)
        bitmap.compress(CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()
        // Close the stream
        try {
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return byteArray
    }

    private fun generatePDF(view: View) {
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap: Bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false

        val pdfFilePath: String = requireActivity().cacheDir.absolutePath.plus("/reports.pdf")

        if (!File(pdfFilePath).exists()) Log.d("PDF", "create file status --> ${File(pdfFilePath).createNewFile()}")

        try {
            val document = Document()
            val fileOutputStream = FileOutputStream(pdfFilePath)
            val pdfWriter = PdfWriter.getInstance(document, fileOutputStream)

            document.open()

            val documentWidth: Float = document.pageSize.width
            val documentHeight: Float = document.pageSize.height

            val bitmapWidth = bitmap.width.toFloat()
            val bitmapHeight = bitmap.height.toFloat()

            val widthScale = documentWidth / bitmapWidth
            val heightScale = documentHeight / bitmapHeight

            // Choose the smaller scale factor to fit the entire bitmap within the page
            val scale = widthScale.coerceAtMost(heightScale)

            // Calculate the scaled width and height
            val scaledWidth = bitmapWidth * scale
            val scaledHeight = bitmapHeight * scale

            val image: Image = Image.getInstance(bitmapToByteArray(bitmap))

            // Set the image scale type to full size (no scaling)
            image.setAbsolutePosition((documentWidth - scaledWidth) / 2, (documentHeight - scaledHeight) / 2)
            image.scaleToFit(scaledWidth, scaledHeight)

            document.add(image)
            document.close()

            pdfWriter.close() // Close the PdfWriter
            fileOutputStream.close() // Close the FileOutputStream

            // Recycle the bitmap to free memory
            bitmap.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val job = Job()
    private val apiScope = CoroutineScope(job + Dispatchers.IO)

    private val TAG = "ReportsFragment"
    private fun generateReport() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialogView: View = layoutInflater.inflate(R.layout.layout_print, null)


        val dialog = builder.create()

        dialog.setView(dialogView)
//        dialog.show()

    }

}