package com.sap.codelab.repository

import androidx.room.Room
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import com.sap.codelab.model.Memo
import kotlinx.coroutines.flow.Flow

private const val DATABASE_NAME: String = "codelab"

/**
 * The repository is used to retrieve data from a data source.
 */
internal class Repository(context: Context) : IMemoRepository {

    private val database: Database = Room.databaseBuilder(context, Database::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration(true)
            .build()

    @WorkerThread
    override fun saveMemo(memo: Memo): Long {
        return database.getMemoDao().insert(memo)
    }

    @WorkerThread
    override fun getOpen(): Flow<List<Memo>> = database.getMemoDao().getOpen()

    @WorkerThread
    override fun getAll(): Flow<List<Memo>> = database.getMemoDao().getAll()

    @WorkerThread
    override fun getMemoById(id: Long): Memo = database.getMemoDao().getMemoById(id)
}