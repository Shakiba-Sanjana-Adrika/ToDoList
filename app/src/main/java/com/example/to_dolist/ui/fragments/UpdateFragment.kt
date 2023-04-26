package com.example.to_dolist.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.to_dolist.R
import com.example.to_dolist.data.localDatabase.TodoListItem
import com.example.to_dolist.databinding.FragmentUpdateBinding
import com.example.to_dolist.ui.MainActivity
import com.example.to_dolist.utils.Constants
import com.example.to_dolist.utils.TimeToStringConverter
import com.example.to_dolist.ui.viewModel.TodoListViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import jhonatan.sabadi.datetimepicker.showDateAndTimePicker
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class UpdateFragment : Fragment() {
    private lateinit var data: String
    private lateinit var binding: FragmentUpdateBinding
    private lateinit var todo: TodoListItem
    private val viewModel: TodoListViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateTodoInDetails()
        initOnClick()
    }

    private fun updateTodoInDetails() {
        arguments?.getString(Constants.DATA_FROM_DETAILS_TO_UPDATE)?.let { data = it }
        val type = object : TypeToken<TodoListItem>() {}.type
        todo = Gson().fromJson(data, type)
        binding.updateTaskTitle.setText(todo.title)
        val datetime = SimpleDateFormat(Constants.USER_TIME_PATTERN).format(todo.time)
        binding.updateTaskDateTime.setText(datetime)
        binding.updateSubTask.setText(todo.todo.joinToString("\n"))

    }

    private fun initOnClick() {
        binding.apply {
            updateDateTimeBtn.setOnClickListener {
                dateTimePicker()
            }
            updateTaskBtn.setOnClickListener {
                val editedList: List<String> =
                    binding.updateSubTask.text.toString().split("\n").dropLastWhile { it == "" }
                val datetime =
                    SimpleDateFormat(Constants.USER_TIME_PATTERN).format(todo.time)

//check anything has been changed or not
                if (
                    todo.title.equals(binding.updateTaskTitle.text.toString()) &&
                    datetime.equals(binding.updateTaskDateTime.text.toString()) &&
                    isEqual(editedList, todo.todo)
                ) {
                    Toast.makeText(context, getString(R.string.no_change), Toast.LENGTH_SHORT)
                        .show()
                } else {
//Check text field is empty or not
                    if (binding.updateTaskTitle.text.toString()
                            .equals("") || binding.updateSubTask.text.toString().equals("")
                    ) {
                        Toast.makeText(
                            context,
                            getString(R.string.cant_empty),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        if (datetime.equals(binding.updateTaskDateTime.text.toString())) {
                            setData(
                                todo.id,
                                todo.time,
                                binding.updateTaskTitle.text.toString(),
                                editedList
                            )
                            findNavController().navigateUp()
                        } else {
                            val time =
                                SimpleDateFormat(Constants.TIME_PATTERN).parse(binding.updateTaskDateTime.text.toString()).time

                            if (time < Calendar.getInstance().timeInMillis) {
                                Toast.makeText(context,
                                    getString(R.string.invalid_time),
                                    Toast.LENGTH_SHORT).show()
                            } else {
                                setData(
                                    todo.id,
                                    time,
                                    binding.updateTaskTitle.text.toString(),
                                    editedList
                                )
                                Toast.makeText(
                                    context,
                                    getString(R.string.successfully_updated),
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigateUp()
                            }
                        }
                    }
                }
            }
        }
    }


    private fun dateTimePicker() {
        (requireActivity() as MainActivity).showDateAndTimePicker { date: Date ->
            binding.updateTaskDateTime.setText(TimeToStringConverter.TimeToString(date.time))
        }
    }

    /**
     * Saving updated data into database
     */
    private fun setData(id: String, time: Long, title: String, list: List<String>) {
        viewModel.updateTodoList(
            TodoListItem(id, time, title, list)
        )
    }

    fun isEqual(first: List<String>, second: List<String>): Boolean {
        if (first.size != second.size) {
            return false
        }
        first.forEachIndexed { index, value ->
            if (second[index] != value) {
                return false
            }
        }
        return true
    }
}
