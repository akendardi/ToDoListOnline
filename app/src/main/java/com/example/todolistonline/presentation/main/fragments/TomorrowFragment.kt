package com.example.todolistonline.presentation.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.databinding.FragmentTomorrowBinding
import com.example.todolistonline.domain.Task
import com.example.todolistonline.presentation.ViewModelFactory
import com.example.todolistonline.presentation.main.MainViewModel
import com.example.todolistonline.presentation.main.recycler_view.OnItemClickListener
import com.example.todolistonline.presentation.main.recycler_view.TasksAdapter
import kotlinx.coroutines.launch
import javax.inject.Inject


class TomorrowFragment : Fragment(), OnItemClickListener {



    private var _binding: FragmentTomorrowBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var tasksAdapter: TasksAdapter


    private val viewModel: MainViewModel by activityViewModels { viewModelFactory }

    private val component by lazy {
        (activity?.application as ToDoListOnlineApp).component
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTomorrowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        lifecycleScope.launch {
            viewModel.getTomorrowTasks()
        }
        setupSwipeToDelete()
        setupRecyclerView()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = tasksAdapter.currentList[position]
                viewModel.deleteTask(task)
                Toast.makeText(context, "Задача удалена", Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun observeViewModel() {
        viewModel.tomorrowList.observe(viewLifecycleOwner){
            tasksAdapter.submitList(it)
        }
    }

    private fun setupRecyclerView() {
        tasksAdapter = TasksAdapter(this)
        binding.recyclerView.apply {
            adapter = tasksAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onItemClick(task: Task) {
        val fragment = TaskFragment.newInstance(task)
        fragment.show(parentFragmentManager, "TaskDialogFragment")
    }

    override fun onItemStateChanged(task: Task) {
        viewModel.updateTask(task)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TodayFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}