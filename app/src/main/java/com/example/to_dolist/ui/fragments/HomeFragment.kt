package com.example.to_dolist.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_dolist.R
import com.example.to_dolist.databinding.FragmentHomeBinding
import com.example.to_dolist.ui.adapter.TodoViewAdapter
import com.example.to_dolist.utils.BroadcastMaker
import com.example.to_dolist.ui.viewModel.TodoListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: TodoListViewModel by viewModels()
    private val todoViewAdapter: TodoViewAdapter by lazy {
        TodoViewAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * For showing greetings it checks the current time and according to that shows greetings
     */
    private fun initGreetings() {
        val c: Calendar = Calendar.getInstance()
        val timeOfDay: Int = c.get(Calendar.HOUR_OF_DAY)
        if (timeOfDay >= 0 && timeOfDay < 5) {
            binding.timeOfDay.setText(getString(R.string.good_night_message))
        } else if (timeOfDay >= 5 && timeOfDay < 12) {
            binding.timeOfDay.setText(getString(R.string.good_morning_message))
        } else if (timeOfDay >= 12 && timeOfDay < 17) {
            binding.timeOfDay.setText(getString(R.string.good_afternoon_message))
        } else if (timeOfDay >= 17 && timeOfDay < 21) {
            binding.timeOfDay.setText(getString(R.string.good_evening_message))
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            binding.timeOfDay.setText(R.string.good_night_message)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initOnClick()
        initGreetings()
        initObserver()
    }

    private fun initOnClick() {
        binding.apply {
            addTask.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_addFragment)
            }
            settingsIcon.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
            }
            syncIcon.setOnClickListener {
                if (isConnectingToInternet()) {
                    viewModel.syncToLocalDB()
                } else {
                    Toast.makeText(context,
                        getString(R.string.no_internet_message),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initRecycler() {

        binding.taskRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoViewAdapter
        }
        viewModel.getAllTodoData.observe(viewLifecycleOwner) {
            todoViewAdapter.addTodos(it)
        }
    }

    private fun initObserver() {
        viewModel.localTodoList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                BroadcastMaker.sendBroadcast(requireContext())
            }
        }
    }

    fun isConnectingToInternet(): Boolean {
        val connectivity =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.allNetworkInfo
        if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
            return true
        }
        return false
    }
}