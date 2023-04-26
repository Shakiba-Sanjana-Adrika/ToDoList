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
import com.example.to_dolist.databinding.FragmentAddBinding
import com.example.to_dolist.ui.MainActivity
import com.example.to_dolist.utils.Constants
import com.example.to_dolist.utils.TimeToStringConverter
import com.example.to_dolist.ui.viewModel.TodoListViewModel
import dagger.hilt.android.AndroidEntryPoint
import jhonatan.sabadi.datetimepicker.showDateAndTimePicker
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private val viewModel: TodoListViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClicks()
    }

    private fun initClicks() {
        binding.apply {
            dateTimeBtn.setOnClickListener() {
                dateTimePicker()
            }
            addTaskBtn.setOnClickListener {
                var allow: Boolean = true
                var message: String = ""

                //check if task title text field is empty or not
                if (binding.addTaskTitle.text.toString().equals("")) {
                    message = getString(R.string.title_message)
                    allow = false
                    binding.addTaskTitle.requestFocus()
                }

                //check if task date time text field is empty or not
                if (binding.taskDateTime.text.toString().equals("")) {
                    if (message.equals("")) {
                        message = message + getString(R.string.time_message)
                    } else {
                        message = message + getString(R.string.time_with_comma)
                    }
                    allow = false
                }

                //check if sub tasks text field is empty or not
                if (binding.subTask.text.toString().equals("")) {
                    if (message.equals("")) {
                        message = message + getString(R.string.subtask_message)
                    } else {
                        message = message + getString(R.string.subtask_with_and)
                    }
                    allow = false
                    binding.subTask.requestFocus()
                }

//check if all text fields are filled then user given todoitem will be saved into databse else shows a toast
                if (allow) {
                    saveUserTodoToDatabase()
                } else {
                    Toast.makeText(context,
                        getString(R.string.please_add_message) + message,
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    /**
     * User given todoitems are saved into local database
     */
    private fun saveUserTodoToDatabase() {
        val subTasks: String = binding.subTask.text.toString()
        val subTaskArray: List<String> = subTasks.split("\n").dropLastWhile { it == "" }
        val timeInMili =
            SimpleDateFormat(Constants.TIME_PATTERN).parse(binding.taskDateTime.text.toString()).time

        //checks if user gives valid time or not
        if (timeInMili < Calendar.getInstance().timeInMillis) {
            Toast.makeText(context, "Invalid time", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.insertTodo(
                TodoListItem(
                    UUID.randomUUID().toString(),
                    timeInMili,
                    binding.addTaskTitle.text.toString(),
                    subTaskArray
                )
            )
            Toast.makeText(activity,
                getString(R.string.sucessful_message),
                Toast.LENGTH_SHORT).show();
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }
    }

    private fun dateTimePicker() {
        (requireActivity() as MainActivity).showDateAndTimePicker { date: Date ->
            binding.taskDateTime.setText(TimeToStringConverter.TimeToString(date.time))
        }
    }
}