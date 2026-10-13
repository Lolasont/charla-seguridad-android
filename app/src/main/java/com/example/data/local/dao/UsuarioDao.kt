package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.AsignaturaEntity
import com.example.data.local.entities.EstudianteEntity
import com.example.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE correo = :correo OR rut = :correo")
    suspend fun getUsuarioByCredencial(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios")
    fun getTodosLosUsuarios(): Flow<List<UsuarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM asignaturas_secciones WHERE docenteRut = :rut")
    fun getAsignaturasByDocente(rut: String): Flow<List<AsignaturaEntity>>

    @Query("SELECT * FROM asignaturas_secciones")
    fun getTodasLasAsignaturas(): Flow<List<AsignaturaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsignatura(asignatura: AsignaturaEntity)

    @Query("SELECT * FROM estudiantes_seccion WHERE seccionId = :seccionId")
    fun getEstudiantesBySeccion(seccionId: String): Flow<List<EstudianteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstudiantes(estudiantes: List<EstudianteEntity>)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun getCount(): Int
}
