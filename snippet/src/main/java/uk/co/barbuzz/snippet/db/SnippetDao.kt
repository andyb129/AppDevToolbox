package uk.co.barbuzz.snippet.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import uk.co.barbuzz.snippet.model.Snippet

@Dao
interface SnippetDao {

    @Query("SELECT * from snippet_table ORDER BY snippet ASC")
    fun getAlphabetizedSnippets(): LiveData<List<Snippet>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(snippet: Snippet)

    @Query("DELETE FROM snippet_table")
    fun deleteAll()

    @Update
    fun update(snippet: Snippet)

    @Delete
    fun delete(snippet: Snippet)
}
