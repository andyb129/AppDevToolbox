package uk.co.barbuzz.appwidget.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val venom = Venom.getGlobalInstance()
        view.findViewById<View>(R.id.start_venom).setOnClickListener {
            venom.start()
        }
        view.findViewById<View>(R.id.stop_venom).setOnClickListener {
            venom.stop()
        }
    }
}
