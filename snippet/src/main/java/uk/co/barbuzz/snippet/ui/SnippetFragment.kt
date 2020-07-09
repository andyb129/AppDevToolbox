package uk.co.barbuzz.snippet.ui

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import uk.co.barbuzz.snippet.R
import uk.co.barbuzz.snippet.db.DatabaseBackup
import uk.co.barbuzz.snippet.db.SnippetRoomDatabase
import uk.co.barbuzz.snippet.model.Snippet
import uk.co.barbuzz.snippet.viewmodel.SnippetViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.channels.FileChannel

class SnippetFragment : Fragment(), SnippetListAdapter.OnSnippetOnClickListener {

    private lateinit var adapter: SnippetListAdapter
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var mSnippetViewModel: SnippetViewModel
    private lateinit var snippetList: List<Snippet>
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_snippet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        constraintLayout = view.findViewById(R.id.constraintLayout)
        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(activity, SnippetActivity::class.java)
            startActivityForResult(intent, NEW_SNIPPET_REQUEST_CODE)
        }

        val emptyListText = view.findViewById<TextView>(R.id.empty_list_text)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        adapter = SnippetListAdapter(context!!, this)
        val linearLayoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            linearLayoutManager.orientation
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addItemDecoration(dividerItemDecoration)

        mSnippetViewModel = ViewModelProvider(this).get(SnippetViewModel::class.java)

        mSnippetViewModel.allSnippets.observe(activity!!, Observer { snippets ->
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_backup -> {
                backupDb()
                true
            }
            R.id.action_restore -> {
                restoreDbIntent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
                adapter.notifyDataSetChanged()
            }
        } else if (requestCode == RESTORE_SNIPPET_DATABASE_REQUEST_CODE) {
            restoreDb(intentData)
            adapter.notifyDataSetChanged()
        } else {
            Snackbar.make(constraintLayout, R.string.not_saved, Snackbar.LENGTH_LONG)
                .setAnchorView(fab)
                .show()
        }
    }

    override fun onSnippetClicked(position: Int) {
        val snippet = snippetList[position]
        val intent = Intent(activity, SnippetActivity::class.java)
        intent.putExtra(SnippetActivity.EXTRA_SNIPPET, snippet)
        startActivityForResult(intent, EDIT_SNIPPET_REQUEST_CODE)
    }

    private fun backupDb() {
        var destDir = ""
        val backupSnippetDatabasePath = DatabaseBackup.backupSnippetDatabase(activity!!, ioScope)
        val hasBackupFailed = backupSnippetDatabasePath.isEmpty()
        val backupMsg = if (hasBackupFailed) {
            getString(R.string.db_backup_fail)
        } else {
            destDir = backupSnippetDatabasePath.substringBeforeLast("Android") + "Download/"
            DatabaseBackup.copyFileOrDirectory(backupSnippetDatabasePath, destDir)
            String.format(getString(R.string.db_backup_success), destDir)
        }
        val alertDialog = AlertDialog.Builder(activity!!)
            .setTitle(getString(R.string.alert_dialog_backup_title))
            .setMessage(backupMsg)
            .setNegativeButton(android.R.string.cancel, null)
        if (!hasBackupFailed) {
            alertDialog.setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                openFolderWithDatabaseBackup(destDir)
            })
        }
        alertDialog.show()
    }

    private fun openFolderWithDatabaseBackup(backupSnippetDatabasePath: String) {
        val selectedUri = Uri.parse(backupSnippetDatabasePath)
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setDataAndType(selectedUri, "*/*")

        if (intent.resolveActivityInfo(activity!!.packageManager, 0) != null) {
            startActivity(intent)
        }
    }

    private fun restoreDbIntent() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "*/*"
        startActivityForResult(Intent.createChooser(i, "Select DB File"), RESTORE_SNIPPET_DATABASE_REQUEST_CODE)
    }

    private fun restoreDb(intentData: Intent?) {
        val fileUri: Uri? = intentData?.data
        try {
            val inputStream: InputStream? = activity!!.contentResolver.openInputStream(fileUri!!)
            restoreDatabase(inputStream)
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun dbRestoredErrorMsg() {
        Snackbar.make(constraintLayout, R.string.db_not_restored, Snackbar.LENGTH_LONG)
            .setAnchorView(fab)
            .show()
    }

    private fun restoreDatabase(inputStreamNewDB: InputStream?) {
        val appDatabase = SnippetRoomDatabase.getDatabase(activity!!, ioScope)
        appDatabase.close()

        val oldDB: File = activity!!.getDatabasePath(SnippetRoomDatabase.DATABASE_NAME)
        if (inputStreamNewDB != null) {
            try {
                copyFile(inputStreamNewDB as FileInputStream, FileOutputStream(oldDB))
                Snackbar.make(constraintLayout, R.string.db_restored, Snackbar.LENGTH_LONG)
                    .setAnchorView(fab)
                    .show()
            } catch (e: IOException) {
                dbRestoredErrorMsg()
            }
        } else {
            dbRestoredErrorMsg()
        }
    }

    @Throws(IOException::class)
    fun copyFile(fromFile: FileInputStream, toFile: FileOutputStream) {
        var fromChannel: FileChannel? = null
        var toChannel: FileChannel? = null
        try {
            fromChannel = fromFile.channel
            toChannel = toFile.channel
            fromChannel.transferTo(0, fromChannel.size(), toChannel)
        } finally {
            try {
                fromChannel?.close()
            } finally {
                toChannel?.close()
            }
        }
    }

    private fun getRandomId(): Long = Math.random().toLong()

}

private const val NEW_SNIPPET_REQUEST_CODE = 1
private const val EDIT_SNIPPET_REQUEST_CODE = 2
private const val RESTORE_SNIPPET_DATABASE_REQUEST_CODE = 3
