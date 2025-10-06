package com.example.krishna_project.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val userId: String,
    val passwordHash: String,
    val role: String // "Founder" or "Investor"
)
