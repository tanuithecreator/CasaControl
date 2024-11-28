package com.example.casacontrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.Intent
import com.bumptech.glide.Glide
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.casacontrol.LoginActivity
import com.example.casacontrol.R
import com.example.casacontrol.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Get the current user
        val user = auth.currentUser

        if (user != null) {
            // Display user's name and email
            binding.nameTextView.text = user.displayName ?: "John Doe"  // Fallback to "John Doe" if display name is null
            binding.emailTextView.text = user.email ?: "No email available"

            // Set up profile image (you can use user's profile photo if available)
            user.photoUrl?.let {
                // Load the profile image using an image loading library like Glide or Picasso
                Glide.with(requireContext()).load(it).into(binding.profileImageView)
            } ?: run {
                // Set a placeholder if no profile image is available
                binding.profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
            }
        } else {
            // If no user is signed in, show a message or handle as needed
            Toast.makeText(context, "No user signed in", Toast.LENGTH_SHORT).show()
        }

        // Handle Edit Profile button click (you can implement the functionality for editing profile)
        binding.editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // Handle Logout button click
        binding.logoutButton.setOnClickListener {
            // Sign out the user
            auth.signOut()
            Toast.makeText(context, "You have logged out", Toast.LENGTH_SHORT).show()

            // Navigate back to the login screen using an Intent
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Close ProfileFragment and prevent user from returning to it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
