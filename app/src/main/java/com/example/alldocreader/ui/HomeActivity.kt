package com.example.alldocreader.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.alldocreader.R
import com.example.alldocreader.databinding.ActivityHomeBinding
import com.example.alldocreader.databinding.ExitDialogBinding
import com.example.alldocreader.helper.Constants.ALL
import com.example.alldocreader.helper.Constants.DOC
import com.example.alldocreader.helper.Constants.PDF
import com.example.alldocreader.helper.Constants.PPT
import com.example.alldocreader.helper.Constants.XLS
import com.example.alldocreader.helper.EventsHandler
import com.example.alldocreader.ui.fragments.home.fragments.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding

    private var _navController: NavController? = null
    private val navController get() = _navController

    private val sharedViewModel by viewModels<SharedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment

        _navController = navHostFragment.navController

        openExternalIntent()

        setupSmoothBottomMenu()

        startCollection()
    }


    private fun openExternalIntent() {

        lifecycleScope.launch {
            sharedViewModel.openExternalIntent(intent)

        }


    }

    @SuppressLint("ResourceAsColor")
    private fun startCollection() {

        lifecycleScope.launchWhenCreated {

            sharedViewModel.event
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {

                    when (it) {
                        is EventsHandler.MoveForward -> {
                            startActivity(it.intent)
                            sharedViewModel.resetValues()
                        }
                        else -> Unit
                    }

                }

        }
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.navbar_menu)
        val menu = popupMenu.menu
        navController?.let { binding?.bottomBar?.setupWithNavController(menu, it) }
    }

    override fun onDestroy() {
        super.onDestroy()

        _navController = null

        _binding = null

    }

    override fun onBackPressed() {
        exitDialog()
    }

    private fun exitDialog() {

        val dialogBinding: ExitDialogBinding = ExitDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)

        if (dialog.window != null)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.apply {
            ok.setOnClickListener {

                finishAndRemoveTask()

            }

            cancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.setCanceledOnTouchOutside(true)

        dialog.show()

    }
}