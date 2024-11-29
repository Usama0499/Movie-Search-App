package com.usama.moviesearchapp.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.usama.moviesearchapp.data.models.db.BookmarkedMovieEntity


@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: BookmarkedMovieEntity)

    @Delete
    suspend fun delete(movie: BookmarkedMovieEntity)

    @Query("SELECT * FROM bookmarked_movies WHERE imdbID = :id")
    suspend fun getBookmarkById(id: String): BookmarkedMovieEntity?

    @Query("SELECT * FROM bookmarked_movies")
    fun getAllBookmarks(): PagingSource<Int, BookmarkedMovieEntity>
}
