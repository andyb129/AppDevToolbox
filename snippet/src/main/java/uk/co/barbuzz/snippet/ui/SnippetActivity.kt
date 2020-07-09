package uk.co.barbuzz.snippet.ui

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import uk.co.barbuzz.snippet.R
import uk.co.barbuzz.snippet.model.Snippet


class SnippetActivity : AppCompatActivity() {

    private var snippetToEdit: Snippet? = null
    private lateinit var editAbbrevView: EditText
    private lateinit var editSnippetView: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_snippet)

        supportActionBar?.setTitle(R.string.app_name)
        editAbbrevView = findViewById(R.id.abbrev_edit_text)
        editSnippetView = findViewById(R.id.snippet_edit_text)

        if (intent != null) {
            snippetToEdit = intent.getParcelableExtra(EXTRA_SNIPPET)
            editAbbrevView.setText(snippetToEdit?.abbreviation)
            editSnippetView.setText(snippetToEdit?.snippet)
        }

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editSnippetView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val abbrev = editAbbrevView.text.toString()
                val snippet = editSnippetView.text.toString()
                if (snippetToEdit != null) {
                    replyIntent.putExtra(EXTRA_SNIPPET, Snippet(snippetToEdit!!.id, abbrev, snippet))
                } else {
                    replyIntent.putExtra(EXTRA_ABBREV, abbrev)
                    replyIntent.putExtra(EXTRA_REPLY, snippet)
                }
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_snippet, menu)
        if (snippetToEdit == null) menu?.findItem(R.id.action_delete)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                deleteSnippet()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteSnippet() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.alert_dialog_title))
            .setMessage(getString(R.string.alert_dialog_message))
            .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                val replyIntent = Intent()
                val abbrev = editAbbrevView.text.toString()
                val snippet = editSnippetView.text.toString()
                replyIntent.putExtra(EXTRA_SNIPPET_DELETE, Snippet(snippetToEdit!!.id, abbrev, snippet))
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            })
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    companion object {
        const val EXTRA_REPLY = "uk.co.barbuzz.snippet.REPLY"
        const val EXTRA_ABBREV = "uk.co.barbuzz.snippet.ABBREV"
        const val EXTRA_SNIPPET = "uk.co.barbuzz.snippet.SNIPPET"
        const val EXTRA_SNIPPET_DELETE = "uk.co.barbuzz.snippet.SNIPPETDELETE"
    }
}
