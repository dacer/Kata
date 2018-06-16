package im.dacer.kata.data.room

import android.arch.persistence.room.*
import im.dacer.kata.data.model.news.NhkNews
import io.reactivex.Flowable

@Dao
interface NhkNewsDao {
    @Query("SELECT * FROM nhk_news ORDER BY pubDate DESC")
    fun loadAll(): Flowable<List<NhkNews>>

    @Query("SELECT * FROM nhk_news WHERE content is null ORDER BY pubDate DESC")
    fun loadAllNoContent(): Flowable<List<NhkNews>>

    @Query("SELECT * FROM nhk_news WHERE id=:id")
    fun get(id: String): Flowable<NhkNews>

    @Query("SELECT * FROM nhk_news WHERE id=:id")
    fun getSync(id: String): NhkNews?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(users: NhkNews)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(users: Array<NhkNews>)

    @Update
    fun updateNewsList(vararg news: NhkNews)

    @Update
    fun updateNews(news: NhkNews)
}
