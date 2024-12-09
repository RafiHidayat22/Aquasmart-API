package com.example.aquasmart.ui.auth.Register

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.aquasmart.R
import com.example.aquasmart.Utils.FormValidator
import com.example.aquasmart.databinding.ActivitySignUpBinding
import com.example.aquasmart.ui.auth.Login.LoginActivity
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels()
    private val formValidator = FormValidator()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        formValidator.setupTextWatchers(
            fullNameInputLayout = binding.fullNameInputLayout,
            emailInputLayout = binding.emailInputLayout,
            phoneInputLayout = binding.phoneInputLayout,
            passwordInputLayout = binding.passwordInputLayout,
            birthDateInputLayout = binding.birthDateInputLayout
        )


        // Observe loading status
        signUpViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe registration status
        signUpViewModel.registrationStatus.observe(this) { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()

            // Jika registrasi berhasil, bersihkan input dan pindah ke LoginActivity
            if (status.contains("success", true)) {
                clearInputFields() // Bersihkan input fields
                navigateToLoginActivity() // Pindah ke LoginActivity
            }
        }

        // Handle login link click
        binding.txtLinkLogin.setOnClickListener {
            navigateToLoginActivity()
        }

        // Handle SignUp button click
        binding.signUpButton.setOnClickListener {
            val fullName = binding.fullNameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val phoneNumber = binding.phoneInput.text.toString().trim()
            val birthDate = binding.birthDateInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            // Validate fields before proceeding
            if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || birthDate.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi data anda terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signUpViewModel.registerUser(fullName, email, birthDate, phoneNumber, password)
        }
        // Set Date Picker for Birth Date input
        binding.birthDateInput.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        // Initialize calendar to today's date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create DatePickerDialog
        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date and set it in the birthDateInput EditText
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                binding.birthDateInput.setText(selectedDate)
            },
            year, month, day
        )

        // Show DatePicker
        datePicker.show()
    }

    private fun clearInputFields() {
        binding.fullNameInput.text?.clear()
        binding.emailInput.text?.clear()
        binding.phoneInput.text?.clear()
        binding.birthDateInput.text?.clear()
        binding.passwordInput.text?.clear()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}