package im.dacer.kata.data.room.dao

import android.arch.persistence.room.*
import im.dacer.kata.data.model.bigbang.Word
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface WordDao {

    @Query("SELECT * FROM word WHERE mastered is 0 OR mastered is NULL ORDER BY updatedAt DESC")
    fun loadNotMastered(): Flowable<List<Word>>

    @Query("SELECT * FROM word WHERE mastered is 1 ORDER BY updatedAt DESC")
    fun loadMastered(): Flowable<List<Word>>

    @Query("SELECT * FROM word WHERE baseForm is :baseForm LIMIT 1")
    fun findByBaseForm(baseForm: String): Maybe<List<Word>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: Word)

    @Update
    fun update(item: Word)

    @Delete
    fun delete(item: Word)
}
