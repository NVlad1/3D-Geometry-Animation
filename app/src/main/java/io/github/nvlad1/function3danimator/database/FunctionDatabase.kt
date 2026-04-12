package io.github.nvlad1.function3danimator.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [FunctionDbEntity::class],
    version = 1
)
abstract class FunctionDatabase: RoomDatabase() {
    abstract fun getFunctionDao(): FunctionDao
}
