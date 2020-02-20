package im.dacer.kata.data.room.dao

import androidx.room.*
import im.dacer.kata.data.model.news.EasyNews
import io.reactivex.Flowable

@Dao
interface EasyNewsDao {
    @Query("SELECT * FROM easy_news ORDER BY news_publication_time DESC")
    fun loadAll(): Flowable<List<EasyNews>>

    @Query("SELECT * FROM easy_news WHERE content is null ORDER BY news_publication_time DESC")
    fun loadAllNoContent(): Flowable<List<EasyNews>>

    @Query("SELECT * FROM easy_news WHERE news_id=:id")
    fun get(id: String): Flowable<EasyNews>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(users: EasyNews)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(users: Array<EasyNews>)

    @Update
    fun updateNewsList(vararg news: EasyNews)

    @Update
    fun updateNews(news: EasyNews)
}
