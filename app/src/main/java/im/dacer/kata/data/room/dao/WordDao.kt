package im.dacer.kata.data.room.dao

import androidx.room.*
import im.dacer.kata.data.model.bigbang.Word
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface WordDao {

    @Query("SELECT * FROM word WHERE mastered is 0 OR mastered is NULL ORDER BY updatedAt DESC")
    fun loadNotMasteredFlowable(): Flowable<List<Word>>

    @Query("SELECT * FROM word WHERE mastered is 0 OR mastered is NULL ORDER BY updatedAt DESC")
    fun loadNotMasteredMaybe(): Maybe<List<Word>>

    @Query("SELECT * FROM word WHERE mastered is 1 ORDER BY updatedAt DESC")
    fun loadMastered(): Flowable<List<Word>>

    @Query("SELECT * FROM word WHERE baseForm is :baseForm LIMIT 1")
    fun findByBaseForm(baseForm: String): Maybe<List<Word>>

    @Query("SELECT COUNT(*) FROM word WHERE mastered is 0 OR mastered is NULL")
    fun getNotMasteredNum(): Int

    @Query("SELECT COUNT(*) FROM word WHERE mastered is 1")
    fun getMasteredNum(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: Word): Long

    @Update
    fun update(item: Word)

    @Update
    fun updateWords(vararg users: Word)

    @Delete
    fun delete(item: Word)
}
