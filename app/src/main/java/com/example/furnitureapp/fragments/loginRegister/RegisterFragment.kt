package com.example.furnitureapp.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.furnitureapp.R
import com.example.furnitureapp.data.User
import com.example.furnitureapp.databinding.FragmentRegisterBinding
import com.example.furnitureapp.util.RegisterValidation
import com.example.furnitureapp.util.Resource
import com.example.furnitureapp.viemodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

private const val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
              binding.tvHaveAnAccount.setOnClickListener {
                  findNavController().navigate(R.id.action_registerFragment2_to_loginFragment)
              }
              binding.apply {
                  btnRegisterRegister.setOnClickListener {
                      val user = User(
                          etFirstNameRegister.text.toString().trim(),
                          etLastNameRegister.text.toString().trim(),
                          etEmailRegister.text.toString().trim()
                      )
                      val password = etPassRegister.text.toString()
                      viewModel.createAccountWithEmailAndPassword(user, password)
                  }
              }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it)
                {
                    is Resource.Loading -> {
                        binding.btnRegisterRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.d("test",it.data.toString())
                        binding.btnRegisterRegister.revertAnimation()
                    }
                    is Resource.Error -> {
                        Log.e(TAG,it.message.toString())
                        binding.btnRegisterRegister.revertAnimation()
                    }

                    else -> {Unit}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect{validation->
                if(validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main)
                    {
                        binding.etEmailRegister.apply {
                            requestFocus()
                            error  = validation.email.message
                        }
                    }
                }

                if(validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main)
                    {
                        binding.etPassRegister.apply {
                            requestFocus()
                            error  = validation.password.message
                        }
                    }
                }
            }
        }
    }
}