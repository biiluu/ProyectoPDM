package com.example.proyectopdm.data.repository

import com.example.proyectopdm.data.dao.UserDao
import com.example.proyectopdm.data.entities.User

class UserRepository(private val userDao: UserDao) {
    suspend fun login(carnet: String, password: String): User? {
        return userDao.login(carnet, password)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserCount(): Int {
        return userDao.getUserCount()
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun getUserByCarnet(carnet: String): User? {
        return userDao.getUserByCarnet(carnet)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}
