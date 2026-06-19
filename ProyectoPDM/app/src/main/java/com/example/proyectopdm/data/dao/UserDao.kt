package com.example.proyectopdm.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectopdm.data.entities.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE carnet = :carnet AND password = :password LIMIT 1")
    suspend fun login(carnet: String, password: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE carnet = :carnet LIMIT 1")
    suspend fun getUserByCarnet(carnet: String): User?
}
