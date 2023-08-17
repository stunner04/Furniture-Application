package com.example.furnitureapp.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.furnitureapp.R
import com.example.furnitureapp.activities.ShoppingActivity
import com.example.furnitureapp.databinding.FragmentLoginBinding
import com.example.furnitureapp.dialog.setUpBottomSheetDialog
import com.example.furnitureapp.util.Resource
import com.example.furnitureapp.viemodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment2)
        }

        binding.apply {
            btnLoginLogin.setOnClickListener {
                val email = etEmailLogin.text.toString().trim()
                val password = etPassLogin.text.toString()
                viewModel.login(email, password)
            }
        }

        binding.tvForgotPasswordLogin.setOnClickListener {
            setUpBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error : ${it.message}", Snackbar.LENGTH_LONG)
                            .show()
                    }

                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Sent reset link to your registered E-Mail",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnLoginLogin.startAnimation()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.btnLoginLogin.revertAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnLoginLogin.revertAnimation()
                        /*
                       -> intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        This line adds flags to the intent to control the behavior of the activity launch.
                       -> Intent.FLAG_ACTIVITY_CLEAR_TASK clears any existing activities from the task stack,
                        removing previous activities.
                       ->Intent.FLAG_ACTIVITY_NEW_TASK creates a new task stack for the launched activity.
                         */
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }

    }
}