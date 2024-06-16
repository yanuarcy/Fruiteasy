package com.dicoding.fruiteasy.model

data class ReportBugRequest(
    val username: String,
    val email: String,
    val rating: String,
    val message: String
)
