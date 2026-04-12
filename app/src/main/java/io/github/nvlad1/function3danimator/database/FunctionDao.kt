package io.github.nvlad1.function3danimator.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface FunctionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(functionDbEntity: FunctionDbEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(functionDbEntitiyList: List<FunctionDbEntity>)

    @Query("SELECT * FROM functions ORDER BY createdAt DESC, id DESC")
    suspend fun getAll(): List<FunctionDbEntity>

    @Query("SELECT * FROM functions WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): FunctionDbEntity?

    @Query("DELETE FROM functions WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM functions")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(functionDbEntityList: List<FunctionDbEntity>) {
        deleteAll()
        insertAll(functionDbEntityList)
    }
}
