package com.tadese.framework.presentation.todo

import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.tadese.R
import com.tadese.business.domain.model.todo.Todo
import com.tadese.framework.presentation.common.changeColor
import com.tadese.util.printLogD
import kotlinx.android.synthetic.main.layout_todo_list_item.view.*
import java.lang.IndexOutOfBoundsException


class TodoListAdapter(
    private val interaction: Interaction? = null,
    private val lifecycleOwner: LifecycleOwner,
    private val selectedTodos: LiveData<ArrayList<Todo>>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Todo>() {

        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_todo_list_item,
                parent,
                false
            ),
            interaction,
            lifecycleOwner,
            selectedTodos
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoteViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Todo>) {
        val commitCallback = Runnable {
            // if process died must restore list position
            // very annoying
            interaction?.restoreListPosition()
        }
        printLogD("listadapter", "size: ${list.size}")
        differ.submitList(list, commitCallback)
    }

    fun getNote(index: Int): Todo? {
        return try{
            differ.currentList[index]
        }catch (e: IndexOutOfBoundsException){
            e.printStackTrace()
            null
        }
    }

    class NoteViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?,
        private val lifecycleOwner: LifecycleOwner,
        private val selectedTodos: LiveData<ArrayList<Todo>>,
    ) : RecyclerView.ViewHolder(itemView)
    {


        private val COLOR_GREY = R.color.app_background_color
        private val COLOR_PRIMARY = R.color.purple_500
        private lateinit var todo: Todo

        fun bind(item: Todo) = with(itemView) {
            setOnClickListener {
                interaction?.onItemSelected(adapterPosition, todo)
            }
            setOnLongClickListener {
                interaction?.activateMultiSelectionMode()
                interaction?.onItemSelected(adapterPosition, todo)
                true
            }
            todo = item
            todo_title.text = item.title
            todo_status.text = if(item.completed) "Completed" else "Uncompleted"

            selectedTodos.observe(lifecycleOwner, Observer { todo ->

                if(todo != null){
                    if(todo.contains(todo)){
                        changeColor(
                            newColor = COLOR_GREY
                        )
                    }
                    else{
                        changeColor(
                            newColor = COLOR_PRIMARY
                        )
                    }
                }else{
                    changeColor(
                        newColor = COLOR_PRIMARY
                    )
                }
            })
        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: Todo)

        fun restoreListPosition()

        fun isMultiSelectionModeEnabled(): Boolean

        fun activateMultiSelectionMode()

        fun isTodoSelected(note: Todo): Boolean
    }

}
