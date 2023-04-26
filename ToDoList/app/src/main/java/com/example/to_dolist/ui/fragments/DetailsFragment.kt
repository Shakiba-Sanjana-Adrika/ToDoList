package com.example.to_dolist.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.to_dolist.R
import com.example.to_dolist.data.localDatabase.TodoListItem
import com.example.to_dolist.databinding.FragmentDetailsBinding
import com.example.to_dolist.utils.Constants
import com.example.to_dolist.ui.viewModel.TodoListViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import androidx.appcompat.app.AlertDialog


@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private lateinit var data: String
    private lateinit var tempTodo: TodoListItem
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var todo: TodoListItem
    private val viewModel: TodoListViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initOnClick()
        initObserver()
    }

    /**
     * Observe the changes made in update fragment and according to that also update the details fragment
     */
    private fun initObserver() {
        viewModel.isGetAllowed.observe(viewLifecycleOwner) {
            if (it) {
                todo = viewModel.tempTodoListItem
                binding.showTaskTitle.setText(todo.title)
                val datetime = SimpleDateFormat(Constants.USER_TIME_PATTERN).format(todo.time)
                val timeList: List<String> = datetime.split(" ")

                binding.showTaskDateTime.setText(timeList[4] + " " + timeList[5] + " " + timeList[0] + " " + timeList[1] + " " + timeList[2] + " " + timeList[3])

                var subTodo: String = ""
                for (thing in todo.todo) {
                    subTodo += "- " + thing + "\n"
                }
                binding.showSubTask.setText(subTodo)
            }
        }
    }

    private fun initView() {
        arguments?.getString(Constants.BUNDLE_NAME)?.let { data = it }
        val type = object : TypeToken<TodoListItem>() {}.type
        tempTodo = Gson().fromJson(data, type)
        viewModel.getTodoById(tempTodo.id)
    }

    private fun initOnClick() {
        binding.apply {
            moreVertical.setOnClickListener {
                val menuItemView =
                    view!!.findViewById<View>(com.example.to_dolist.R.id.moreVertical)
                val popupMenu = PopupMenu(activity!!, menuItemView)
                popupMenu.menuInflater.inflate(com.example.to_dolist.R.menu.popup_menu,
                    popupMenu.menu)

//Delete confirmation popup menu
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.deleteBtn -> {
                            AlertDialog.Builder(activity!!)
                                .setTitle(getString(R.string.confirm_delete_message))
                                .setIcon(R.drawable.ic_warning)
                                .setMessage(getString(R.string.confirmation_message))
                                .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                                    viewModel.deleteTodoList(todo)
                                    Toast.makeText(activity,
                                        getString(R.string.successfully_deleted),
                                        Toast.LENGTH_SHORT).show();
                                    findNavController().navigateUp()
                                }
                                .setNegativeButton(getString(R.string.no), null).show()
                        }
                        R.id.editBtn -> {
                            val dataFromDetailsToUpdate =
                                bundleOf(Constants.DATA_FROM_DETAILS_TO_UPDATE to Gson().toJson(todo))
                            findNavController().navigate(R.id.action_detailsFragment_to_updateFragment,
                                dataFromDetailsToUpdate)
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }
}