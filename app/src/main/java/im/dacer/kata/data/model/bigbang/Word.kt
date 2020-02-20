package im.dacer.kata.data.model.bigbang;

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Dacer on 06/10/2018.
 */

@Entity(tableName = "word")
data class Word(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                var baseForm: String,
                var mastered: Boolean? = false,
                var queryTimes: Int = 1,
                var createdAt: Long? = System.currentTimeMillis(),
                var updatedAt: Long? = System.currentTimeMillis()) {

    fun afterSearchAgain(): Word {
        queryTimes++
        mastered = false
        freshUpdatedAt()
        return this
    }

    fun markMastered(): Word {
        mastered = true
        freshUpdatedAt()
        return this
    }

    fun markLearning(): Word {
        mastered = false
        freshUpdatedAt()
        return this
    }

    private fun freshUpdatedAt() {
        updatedAt = System.currentTimeMillis()
    }
}