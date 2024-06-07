package com.dicoding.fruiteasy.model

data class ResetPasswordRequest (
    val uidLocal : String,
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)