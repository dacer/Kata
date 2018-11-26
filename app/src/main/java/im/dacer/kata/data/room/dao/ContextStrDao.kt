package im.dacer.kata.data.room.dao

import android.arch.persistence.room.*
import im.dacer.kata.data.model.bigbang.ContextStr
import io.reactivex.Maybe

@Dao
interface ContextStrDao {

    @Query("SELECT * FROM context_str WHERE wordId is :id")
    fun findByWordId(id: Long): Maybe<List<ContextStr>>

    @Query("SELECT * FROM context_str WHERE text is :text AND fromIndex is :fromIndex AND toIndex is :toIndex")
    fun findByWord(text: String, fromIndex: Int, toIndex: Int): List<ContextStr>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: ContextStr): Long

    @Update
    fun update(item: ContextStr)

    @Delete
    fun delete(item: ContextStr)
}
