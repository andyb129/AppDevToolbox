package uk.co.barbuzz.snippet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.barbuzz.snippet.db.SnippetRepository
import uk.co.barbuzz.snippet.db.SnippetRoomDatabase
import uk.co.barbuzz.snippet.model.Snippet

class SnippetViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: SnippetRepository
    var allSnippets: LiveData<List<Snippet>>

    init {
        val snippetsDao = SnippetRoomDatabase.getDatabase(application, viewModelScope).snippetDao()
        repository = SnippetRepository(snippetsDao)
        allSnippets = repository.allSnippets
    }

    fun insert(snippet: Snippet) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(snippet)
    }

    fun update(snippet: Snippet) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(snippet)
    }

    fun delete(snippet: Snippet) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(snippet)
    }
}
