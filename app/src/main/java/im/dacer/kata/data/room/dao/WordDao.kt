package im.dacer.kata.data.room.dao

import android.arch.persistence.room.*
import im.dacer.kata.data.model.bigbang.Word
import io.reactivex.Flowable

@Dao
interface WordDao {

    @Query("SELECT * FROM word WHERE mastered is 0 OR mastered is NULL ORDER BY updatedAt DESC")
    fun loadNotMastered(): Flowable<List<Word>>

    @Query("SELECT * FROM word WHERE mastered is 1 ORDER BY updatedAt DESC")
    fun loadMastered(): Flowable<List<Word>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: Word)

    @Update
    fun update(item: Word)

    @Delete
    fun delete(item: Word)
}
