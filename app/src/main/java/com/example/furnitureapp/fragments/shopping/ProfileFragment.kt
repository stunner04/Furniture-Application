package com.example.furnitureapp.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.furnitureapp.R
import com.example.furnitureapp.activities.LoginRegisterActivity
import com.example.furnitureapp.databinding.FragmentProfileBinding
import com.example.furnitureapp.util.Resource
import com.example.furnitureapp.util.showBottomNavigationView
import com.example.furnitureapp.viemodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel by viewModels<ProfileViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // Load the User data getting from the firestore and update the UI based on conditions
        lifecycleScope.launchWhenStarted {
            profileViewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        binding.apply {
                            Glide.with(requireView()).load(it.data!!.imagePath).error(
                                ColorDrawable(
                                    Color.BLACK
                                )
                            ).into(imageUser)
                            tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                            tvEmail.text = it.data.email
                        }
                    }

                    is Resource.Error -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }

        binding.imgAccountarrow.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }
        binding.imgAllOrdersArrow.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }

        // Sending the billingData and go to billing fragment from profile fragment
        binding.imgBillingArrow.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                0f,
                emptyArray(),false
            )
            findNavController().navigate(action)
        }

        binding.linearLogOut.setOnClickListener {
            profileViewModel.logOutUser()
            val i = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }

        //binding.tvVersion.text  = "Version ${BuildConfig.VERSION_CODE}"

        // LogOut from the user account
        onLogoutClick()
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

    private fun onLogoutClick() {
        binding.linearLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, LoginRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}