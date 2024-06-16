package com.dicoding.fruiteasy.model

data class ContactUsRequest(
    val name: String,
    val email: String,
    val phone: String,
    val message: String
)
