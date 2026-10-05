package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.InacapRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class InacapApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { InacapRepository(database.charlaDao(), database.artDao()) }

    override fun onCreate() {
        super.onCreate()
        // Prepopulate sample data only the first time installation (if empty)
        applicationScope.launch {
            repository.inicializarDatosSiEsNecesario()
        }
    }
}
