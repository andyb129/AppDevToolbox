package uk.co.barbuzz.snippet.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uk.co.barbuzz.snippet.R
import uk.co.barbuzz.snippet.model.Snippet

class SnippetListAdapter internal constructor(context: Context, private val onSnippetOnClickListener: OnSnippetOnClickListener) :
    RecyclerView.Adapter<SnippetListAdapter.SnippetViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var snippets = emptyList<Snippet>()

    inner class SnippetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        private lateinit var mOnSnippetOnClickListener: OnSnippetOnClickListener
        val abbrevItemView: TextView = itemView.findViewById(R.id.abbrev_text_view)
        val snippetItemView: TextView = itemView.findViewById(R.id.snippet_text_view)

        constructor(itemView: View, onSnippetOnClickListener: OnSnippetOnClickListener) : this(itemView) {
            itemView.setOnClickListener(this)
            this.mOnSnippetOnClickListener = onSnippetOnClickListener
        }

        override fun onClick(view: View?) {
            mOnSnippetOnClickListener.onSnippetClicked(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnippetViewHolder {
        val itemView = inflater.inflate(R.layout.snippet_item_row, parent, false)
        return SnippetViewHolder(itemView, onSnippetOnClickListener)
    }

    override fun onBindViewHolder(holder: SnippetViewHolder, position: Int) {
        val current = snippets[position]
        holder.abbrevItemView.text = current.abbreviation
        holder.snippetItemView.text = current.snippet

    }

    internal fun setSnippets(snippets: List<Snippet>) {
        this.snippets = snippets
        notifyDataSetChanged()
    }

    override fun getItemCount() = snippets.size

    interface OnSnippetOnClickListener {
        fun onSnippetClicked(position: Int)
    }
}
