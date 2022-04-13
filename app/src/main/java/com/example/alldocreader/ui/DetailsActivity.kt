package com.example.alldocreader.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.alldocreader.R
import com.example.alldocreader.databinding.ActivityDetailsBinding
import com.example.alldocreader.helper.Constants
import com.example.alldocreader.helper.Constants.FILE_NAME
import com.example.alldocreader.helper.Constants.FILE_PATH
import com.example.alldocreader.helper.Constants.FILE_SIZE

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private var name: String? = null
    private var path: String? = null
    private var mime: String? = null
    private var size: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getIntentData()
        setText()
        onClick()
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getIntentData() {
        name = intent.extras?.getString(FILE_NAME)
        mime = intent.extras?.getString("mime")
        path = intent.extras?.getString(FILE_PATH)
        size = intent.extras?.getString(FILE_SIZE)

        when (mime) {

            Constants.DOC -> {
                binding.ivFile.setImageResource(R.drawable.ic_doc_icon)
            }

            Constants.DOCX -> {
                binding.ivFile.setImageResource(R.drawable.ic_doc_icon)
            }

            Constants.PPT -> {
                binding.ivFile.setImageResource(R.drawable.ic_ppt_icon)
            }

            Constants.PPTX -> {
                binding.ivFile.setImageResource(R.drawable.ic_ppt_icon)
            }

            Constants.XLS -> {
                binding.ivFile.setImageResource(R.drawable.excel_icon)
            }

            Constants.XLSX -> {
                binding.ivFile.setImageResource(R.drawable.excel_icon)
            }

            Constants.PDF -> {
                binding.ivFile.setImageResource(R.drawable.ic_pdf_icon)
            }

        }
    }

    private fun setText() {
        binding.apply {
            tvFile.text = name
            tvSize.text = size
            tvLocation.text = path
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}