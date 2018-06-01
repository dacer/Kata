package im.dacer.kata.data

import android.content.Context
import im.dacer.kata.data.local.JMDictDbHelper
import timber.log.Timber
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Created by Dacer on 10/01/2018.
 * import JMDict.sqlite3
 */

class DictImporter(private val context: Context) : JMDictDbHelper(context) {

    /**
     * return false if db is existed
     */
    fun importDataBaseFromAssets(): Boolean {
        val myInput = getFileFromZip(context.assets.open(ASSET_DB_FILE_NAME))

        val myOutput = FileOutputStream(dbFile)
        val buffer = ByteArray(1024)
        var length: Int = myInput.read(buffer)
        while (length > 0) {
            myOutput.write(buffer, 0, length)
            length = myInput.read(buffer)
        }
        myOutput.flush()
        myOutput.close()
        myInput.close()
        return true
    }

    @Throws(IOException::class)
    private fun getFileFromZip(zipFileStream: InputStream): ZipInputStream {
        val zis = ZipInputStream(zipFileStream)
        val ze = zis.nextEntry
        if (ze != null) {
            Timber.i("extracting file: ${ze.name}")
        }
        return zis
    }

    companion object {
        val ASSET_DB_FILE_NAME = "$DB_NAME.sqlite3.zip"
    }
}