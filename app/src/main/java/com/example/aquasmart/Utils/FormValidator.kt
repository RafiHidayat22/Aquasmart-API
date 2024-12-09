package com.example.aquasmart.Utils

import android.text.Editable
import android.text.TextWatcher
import com.example.aquasmart.R
import com.google.android.material.textfield.TextInputLayout

class FormValidator {

    fun setupTextWatchers(
        fullNameInputLayout: TextInputLayout? = null,
        emailInputLayout: TextInputLayout? = null,
        phoneInputLayout: TextInputLayout? = null,
        passwordInputLayout: TextInputLayout? = null,
        birthDateInputLayout: TextInputLayout? = null
    ) {
        fullNameInputLayout?.editText?.addTextChangedListener(
            createTextWatcher(fullNameInputLayout, "Nama lengkap harus diisi")
        )
        emailInputLayout?.editText?.addTextChangedListener(createEmailTextWatcher(emailInputLayout))

        phoneInputLayout?.editText?.addTextChangedListener(
            createPhoneNumberTextWatcher(phoneInputLayout)
        )

        // Validasi untuk Password
        passwordInputLayout?.editText?.addTextChangedListener(
            createPasswordTextWatcher(passwordInputLayout)
        )

        // Validasi untuk Tanggal Lahir
        birthDateInputLayout?.editText?.addTextChangedListener(
            createTextWatcher(birthDateInputLayout, "Tanggal lahir harus diisi")
        )
    }

    private fun createTextWatcher(inputLayout: TextInputLayout, errorMessage: String): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().trim()
                if (input.isEmpty()) {
                    inputLayout.error = errorMessage
                    inputLayout.setEndIconDrawable(null)
                } else {
                    inputLayout.error = null
                    inputLayout.setEndIconDrawable(R.drawable.check)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    //  validasi email
    private fun createEmailTextWatcher(inputLayout: TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                when {
                    email.isEmpty() -> {
                        inputLayout.error = "Email harus diisi"
                        inputLayout.setEndIconDrawable(null)
                    }
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        inputLayout.error = "Email tidak valid"
                        inputLayout.setEndIconDrawable(null)
                    }
                    else -> {
                        inputLayout.error = null
                        inputLayout.setEndIconDrawable(R.drawable.check)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    // validasi nomor telepon
    private fun createPhoneNumberTextWatcher(inputLayout: TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val phoneNumber = s.toString().trim()
                when {
                    phoneNumber.isEmpty() -> {
                        inputLayout.error = "Nomor telepon harus diisi"
                        inputLayout.setEndIconDrawable(null)
                    }
                    phoneNumber.length < 10 || phoneNumber.length > 15 -> {
                        inputLayout.error = "Nomor telepon harus antara 10 hingga 15 karakter"
                        inputLayout.setEndIconDrawable(null)
                    }
                    else -> {
                        inputLayout.error = null
                        inputLayout.setEndIconDrawable(R.drawable.check)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    //  validasi password
    private fun createPasswordTextWatcher(inputLayout: TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString().trim()
                when {
                    password.isEmpty() -> {
                        inputLayout.error = "Password harus diisi"
                        inputLayout.setEndIconMode(TextInputLayout.END_ICON_NONE) // Hilangkan ikon
                    }
                    password.length < 8 -> {
                        inputLayout.error = "Password minimal 8 karakter"
                        inputLayout.setEndIconMode(TextInputLayout.END_ICON_NONE) // Hilangkan ikon
                    }
                    else -> {
                        inputLayout.error = null
                        inputLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE) // Munculkan kembali ikon default
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }
}
