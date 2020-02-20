package im.dacer.kata.data.model.bigbang;

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Dacer on 09/10/2018.
 *
 * For example:
 * "ipsum" in
 * Lorem ipsum dolor sit amet, consectetur adipiscing elit
 *
 *
 * fromIndex: 6
 * toIndex: 11
 */

@Entity(tableName = "context_str")
data class ContextStr(@PrimaryKey(autoGenerate = true) val id: Long = 0,
                      val wordId: Long,
                      val text: String,
                      val fromIndex: Int,
                      val toIndex: Int,
                      val createdAt: Long? = System.currentTimeMillis(),
                      val updatedAt: Long? = System.currentTimeMillis()) {

}