package uk.co.barbuzz.snippet.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import uk.co.barbuzz.snippet.R
import uk.co.barbuzz.snippet.model.Snippet
import uk.co.barbuzz.snippet.viewmodel.SnippetViewModel

class SnippetFragment : Fragment(), SnippetListAdapter.OnSnippetOnClickListener {

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var mSnippetViewModel: SnippetViewModel
    private lateinit var snippetList: List<Snippet>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_snippet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintLayout = view.findViewById(R.id.constraintLayout)
        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(activity, SnippetActivity::class.java)
            startActivityForResult(intent, NEW_SNIPPET_REQUEST_CODE)
        }

        val emptyListText = view.findViewById<TextView>(R.id.empty_list_text)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = SnippetListAdapter(context!!, this)
        val linearLayoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            linearLayoutManager.orientation
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addItemDecoration(dividerItemDecoration)

        mSnippetViewModel = ViewModelProvider(this).get(SnippetViewModel::class.java)

        mSnippetViewModel.allSnippets.observe(this, Observer { snippets ->
            if (snippets.isNotEmpty()) {
                emptyListText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                emptyListText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            snippetList = snippets
            snippets.let { adapter.setSnippets(it) }
        })
    }

    override fun onSnippetClicked(position: Int) {
        val snippet = snippetList[position]
        val intent = Intent(activity, SnippetActivity::class.java)
        intent.putExtra(SnippetActivity.EXTRA_SNIPPET, snippet)
        startActivityForResult(intent, EDIT_SNIPPET_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == NEW_SNIPPET_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val snippet = Snippet(
                    getRandomId(),
                    data.getStringExtra(SnippetActivity.EXTRA_ABBREV),
                    data.getStringExtra(SnippetActivity.EXTRA_REPLY)
                )
                mSnippetViewModel.insert(snippet)
                Unit
            }
        } else if (requestCode == EDIT_SNIPPET_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val snippetToEdit = data.getParcelableExtra<Snippet>(SnippetActivity.EXTRA_SNIPPET)
                val snippetToDelete = data.getParcelableExtra<Snippet>(SnippetActivity.EXTRA_SNIPPET_DELETE)
                if (snippetToEdit != null) {
                    mSnippetViewModel.update(snippetToEdit)
                } else {
                    mSnippetViewModel.delete(snippetToDelete)
                }
            }
        } else {
            Snackbar.make(constraintLayout, R.string.not_saved, Snackbar.LENGTH_LONG)
                .setAnchorView(fab)
                .show()
        }
    }

    private fun getRandomId(): Long = Math.random().toLong()

}

private const val NEW_SNIPPET_REQUEST_CODE = 1
private const val EDIT_SNIPPET_REQUEST_CODE = 2
