package uk.co.barbuzz.snippet.db

import android.content.Context
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DatabaseBackup {

    private const val LOGGER = "DatabaseBackup"
    private const val MAXIMUM_DATABASE_FILE = 10
    const val FILE_NAME = "snippetdb_"
    const val FOLDER = "SnippetBackup"

    fun backupSnippetDatabase(context: Context, scope: CoroutineScope?) : String {
        //Checking the availability state of the External Storage.
        val state: String = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED != state) {
            return ""
        }

        //Create a new file that points to the root directory, with the given name
        val appDatabase = SnippetRoomDatabase.getDatabase(context, scope!!)
        appDatabase.close()
        val dbfile = context.getDatabasePath(SnippetRoomDatabase.DATABASE_NAME)
        val sdir = File(context.getExternalFilesDir(null), FOLDER)
        val fileName =
            FILE_NAME + getDateFromMillisForBackup(System.currentTimeMillis())
        val sfpath = sdir.path + File.separator + fileName
        if (!sdir.exists()) {
            sdir.mkdirs()
        } else {
            checkAndDeleteBackupFile(sdir, sfpath)
        }
        val savefile = File(sfpath)
        if (savefile.exists()) {
            Log.d(LOGGER, "File exists. Deleting it and then creating new file.")
            savefile.delete()
        }
        try {
            if (savefile.createNewFile()) {
                val buffersize = 8 * 1024
                val buffer = ByteArray(buffersize)
                var bytes_read = buffersize
                val savedb: OutputStream = FileOutputStream(sfpath)
                val indb: InputStream = FileInputStream(dbfile)
                while (indb.read(buffer, 0, buffersize).also { bytes_read = it } > 0) {
                    savedb.write(buffer, 0, bytes_read)
                }
                savedb.flush()
                indb.close()
                savedb.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(LOGGER, "ex: $e")
            return ""
        }
        return sfpath
    }

    private fun getDateFromMillisForBackup(currentTimeMillis: Long): String {
        val date = Date()
        date.time = currentTimeMillis
        return SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(date)
    }

    private fun checkAndDeleteBackupFile(directory: File, path: String?) {
        val currentDateFile = File(path)
        var fileIndex = -1
        var lastModifiedTime = System.currentTimeMillis()
        if (!currentDateFile.exists()) {
            val files = directory.listFiles()
            if (files != null && files.size >= MAXIMUM_DATABASE_FILE) {
                for (i in files.indices) {
                    val file = files[i]
                    val fileLastModifiedTime = file.lastModified()
                    if (fileLastModifiedTime < lastModifiedTime) {
                        lastModifiedTime = fileLastModifiedTime
                        fileIndex = i
                    }
                }
                if (fileIndex != -1) {
                    val deletingFile = files[fileIndex]
                    if (deletingFile.exists()) {
                        deletingFile.delete()
                    }
                }
            }
        }
    }

    fun copyFileOrDirectory(srcDir: String?, dstDir: String?) {
        try {
            val src = File(srcDir)
            val dst = File(dstDir, src.name)
            if (src.isDirectory) {
                val files = src.list()
                val filesLength = files.size
                for (i in 0 until filesLength) {
                    val src1 = File(src, files[i]).path
                    val dst1 = dst.path
                    copyFileOrDirectory(src1, dst1)
                }
            } else {
                copyFile(src, dst)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, destFile: File) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
        } finally {
            if (source != null) {
                source.close()
            }
            if (destination != null) {
                destination.close()
            }
        }
    }
}
