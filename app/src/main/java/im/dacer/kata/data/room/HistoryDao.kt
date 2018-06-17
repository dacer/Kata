package im.dacer.kata.data.room

import android.arch.persistence.room.*
import im.dacer.kata.data.model.bigbang.History
import io.reactivex.Flowable

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history WHERE star is 0 OR star is NULL ORDER BY createdAt DESC LIMIT :limit")
    fun loadUnstarredLimit(limit: Int): Flowable<List<History>>

    @Query("SELECT * FROM history WHERE star is 1 ORDER BY createdAt DESC")
    fun loadAllAllStarred(): Flowable<List<History>>

    @Query("SELECT * FROM history WHERE star is 0 OR star is NULL ORDER BY createdAt DESC LIMIT :limit")
    fun loadUnstarredLimitSync(limit: Int): List<History>

    @Query("SELECT * FROM history WHERE star is 1 ORDER BY createdAt DESC")
    fun loadAllAllStarredSync(): List<History>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: History)

    @Update
    fun update(item: History)

    @Delete
    fun delete(item: History)
}
