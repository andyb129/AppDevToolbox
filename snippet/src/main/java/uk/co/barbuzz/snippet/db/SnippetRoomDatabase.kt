package uk.co.barbuzz.snippet.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import uk.co.barbuzz.snippet.model.Snippet

@Database(entities = [Snippet::class], version = 1)
abstract class SnippetRoomDatabase : RoomDatabase() {

    abstract fun snippetDao(): SnippetDao

    companion object {
        @Volatile
        private var INSTANCE: SnippetRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): SnippetRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SnippetRoomDatabase::class.java,
                    "snippet_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(
                    SnippetDatabaseCallback(
                        scope
                    )
                )
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class SnippetDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // used for initial testing
                /*INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.snippetDao())
                    }
                }*/
            }
        }

        fun populateDatabase(snippetDao: SnippetDao) {
            snippetDao.deleteAll()

            var snippet = Snippet(1, "He","Hello")
            snippetDao.insert(snippet)
            snippet = Snippet(2, "Wrld", "World!")
            snippetDao.insert(snippet)
        }
    }

}
