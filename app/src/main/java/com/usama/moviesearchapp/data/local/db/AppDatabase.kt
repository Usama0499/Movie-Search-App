package com.usama.moviesearchapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.usama.moviesearchapp.data.local.db.dao.BookmarkDao
import com.usama.moviesearchapp.data.models.db.BookmarkedMovieEntity

@Database(entities = [BookmarkedMovieEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}