package im.dacer.kata.data.model.bigbang;

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Dacer on 13/02/2018.
 */

@Entity(tableName = "history")
data class History(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                   var text: String? = null,
                   var alias: String? = null,
                   var star: Boolean? = false,
                   var createdAt: Long? = System.currentTimeMillis()) {

}