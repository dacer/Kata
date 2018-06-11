package im.dacer.kata.data.room

import android.arch.persistence.room.*
import im.dacer.kata.data.model.news.EasyNews
import io.reactivex.Flowable

@Dao
interface NewsDao {
    @Query("SELECT * FROM easy_news ORDER BY news_publication_time DESC")
    fun loadAll(): Flowable<List<EasyNews>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(users: EasyNews)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(users: Array<EasyNews>)

    @Update
    fun updateUsers(vararg users: EasyNews)
}
