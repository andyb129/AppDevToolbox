package uk.co.barbuzz.snippet.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import uk.co.barbuzz.snippet.model.Snippet

internal class SnippetRepository(private val snippetDao: SnippetDao) {

    val allSnippets: LiveData<List<Snippet>> = snippetDao.getAlphabetizedSnippets()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(snippet: Snippet) {
        snippetDao.insert(snippet)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(snippet: Snippet) {
        snippetDao.update(snippet)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(snippet: Snippet) {
        snippetDao.delete(snippet)
    }
}
