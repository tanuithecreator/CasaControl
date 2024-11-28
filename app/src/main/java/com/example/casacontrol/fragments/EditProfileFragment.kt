package com.example.casacontrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.casacontrol.R
import com.example.casacontrol.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Prefill current user's data
        val user = auth.currentUser
        user?.let {
            binding.nameEditText.setText(it.displayName ?: "")
            binding.emailEditText.setText(it.email ?: "")
        }

        // Handle save button click to update profile
        binding.saveButton.setOnClickListener {
            val updatedName = binding.nameEditText.text.toString().trim()
            val updatedEmail = binding.emailEditText.text.toString().trim()

            // Validate inputs
            if (updatedName.isEmpty() || updatedEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update profile
            updateUserProfile(updatedName, updatedEmail)
        }

        // Back button click to navigate back to the previous page
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateUserProfile(name: String, email: String) {
        val user = auth.currentUser

        if (user != null) {
            // Update Display Name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Optionally update email
                    updateUserEmail(email)
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserEmail(email: String) {
        val user = auth.currentUser

        user?.updateEmail(email)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp() // Navigate back to the previous fragment
            } else {
                Toast.makeText(requireContext(), "Failed to update email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
