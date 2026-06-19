package com.example.proyectopdm.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val carnet: String,
    val password: String,
    val name: String,
    val career: String,
    val hasAcceptedTerms: Boolean = false
)
