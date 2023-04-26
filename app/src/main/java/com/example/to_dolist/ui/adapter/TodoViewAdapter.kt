package com.example.to_dolist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.R
import com.example.to_dolist.data.localDatabase.TodoListItem
import com.example.to_dolist.databinding.RecyclerviewRowBinding
import com.example.to_dolist.utils.Constants
import com.google.gson.Gson
import java.text.SimpleDateFormat

class TodoViewAdapter : RecyclerView.Adapter<TodoViewAdapter.TodoViewHolder>() {
    private val todos = mutableListOf<TodoListItem>()
    fun addTodos(todo: List<TodoListItem>) {
        todos.clear()
        todos.addAll(todo)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TodoViewAdapter.TodoViewHolder {
        val bind =
            RecyclerviewRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(bind)
    }

    inner class TodoViewHolder(private val binding: RecyclerviewRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: TodoListItem) {
            binding.apply {

                //Adding the data into recycler view
                todoTitle.text = todo.title
                val datetime = SimpleDateFormat(Constants.USER_TIME_PATTERN).format(todo.time)
                val dateTimeList: List<String> = datetime.split(" ")
                time.text = dateTimeList[4] + " " + dateTimeList[5]
                dayOfWeek.text = dateTimeList[0]
                date.text = dateTimeList[1]
                month.text = dateTimeList[2]
                year.text = dateTimeList[3]

//By clicking on each todoitem it goes to the details fragment with the details of that particular todoitem
                recyclerItem.setOnClickListener {
                    val data = bundleOf(Constants.BUNDLE_NAME to Gson().toJson(todo))
                    it.findNavController()
                        .navigate(R.id.action_homeFragment_to_detailsFragment, data)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: TodoViewAdapter.TodoViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int {
        return todos.size
    }
}