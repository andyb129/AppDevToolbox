package uk.co.barbuzz.snippet.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
import android.view.accessibility.AccessibilityNodeInfo
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import uk.co.barbuzz.snippet.db.SnippetRepository
import uk.co.barbuzz.snippet.db.SnippetRoomDatabase
import uk.co.barbuzz.snippet.model.Snippet


class SnippetTextAccessibilityService: AccessibilityService() {

    private lateinit var pasteText: String
    private lateinit var allSnippets: List<Snippet>
    private lateinit var editTextAccessibilityNodeInfo: AccessibilityNodeInfo
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()

        //get list of abbreviations + text from db to match
        val snippetDao = SnippetRoomDatabase.getDatabase(application, ioScope).snippetDao()
        val repository = SnippetRepository(snippetDao)
        repository.allSnippets.observeForever(Observer<List<Snippet>>() {
            allSnippets = it
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // if intent received from OverlaySnippetPasteService then paste text passed
        if (intent != null && intent.hasExtra(PASTE_TEXT_EXTRA)) {
            pasteFullText()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent?) {
        if (accessibilityEvent == null || accessibilityEvent.packageName == "uk.co.barbuzz.snippet") return;

        //inspect app elements if ready
        val rootInActiveWindow = rootInActiveWindow
        if (rootInActiveWindow != null) {
            if (accessibilityEvent.eventType == TYPE_VIEW_TEXT_CHANGED) {
                for (snippet in allSnippets) {
                    val text = accessibilityEvent.text[0]
                    val snippetList: List<String> = text.split("\\s".toRegex())
                    val line = snippetList[snippetList.lastIndex]

                    //TODO - work out how to get the line the cursor is on

                    //if abbreviation found in EditText LAST WORD then show paste overlay
                    if (line == snippet.abbreviation) {
                        pasteText = snippet.snippet

                        editTextAccessibilityNodeInfo = accessibilityEvent.source
                        val rect = Rect()
                        editTextAccessibilityNodeInfo.getBoundsInScreen(rect)
                        val prefs = OverlaySnippetPastePreference(this)
                        prefs.setPastTextCoordinates(Pair(rect.left, rect.top))
                        prefs.setPasteText(pasteText)

                        //this shows the overlay so user can paste the text
                        startService(Intent(this, OverlaySnippetPasteService::class.java))
                        break
                    }

                }
            }
        }
    }

    private fun pasteFullText() {
        //TODO - work out how to replace line with cursor in multiline text

        val arguments = Bundle()
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, pasteText)
        editTextAccessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        stopService(Intent(this, OverlaySnippetPasteService::class.java))
    }

    override fun onInterrupt() {
    }

    companion object {
        private const val PASTE_TEXT_EXTRA = "paste_text"

        fun getPasteTextIntent(context: Context): Intent {
            val intent = Intent(context, SnippetTextAccessibilityService::class.java)
            intent.putExtra(PASTE_TEXT_EXTRA, true)
            return intent
        }
    }

}
