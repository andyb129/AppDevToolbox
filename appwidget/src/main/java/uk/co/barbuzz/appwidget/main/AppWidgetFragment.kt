package uk.co.barbuzz.appwidget.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.github.venom.Venom
import uk.co.barbuzz.appwidget.R

class AppWidgetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appdevwidget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputMethodManager = activity?.getSystemService( Context.INPUT_METHOD_SERVICE ) as InputMethodManager
        if( inputMethodManager.isAcceptingText )
            inputMethodManager.hideSoftInputFromWindow( activity?.currentFocus?.windowToken,0)

        val venom = Venom.getGlobalInstance()
        view.findViewById<View>(R.id.start_venom).setOnClickListener {
            venom.start()
        }
        view.findViewById<View>(R.id.stop_venom).setOnClickListener {
            venom.stop()
        }
    }
}
