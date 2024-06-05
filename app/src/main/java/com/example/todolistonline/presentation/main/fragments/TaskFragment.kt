package com.example.todolistonline.presentation.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.todolistonline.R
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.databinding.FragmentTaskBinding
import com.example.todolistonline.data.entities.TaskLocalDbModel
import com.example.todolistonline.domain.TaskRepository
import com.example.todolistonline.domain.TaskState
import com.example.todolistonline.domain.use_cases.tasks_use_cases.AddNewTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class TaskFragment : DialogFragment() {

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var repository: TaskRepository

    @Inject
    lateinit var addNewTaskUseCase: AddNewTaskUseCase

    private val component by lazy {
        (activity?.application as ToDoListOnlineApp).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_App_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun listeners() {
        binding.buttonSave.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    repository.insertTask(
                        TaskLocalDbModel(
                            title = "ХУЙ",
                            priority = 413,
                            time = 1212,
                            state = TaskState.None
                        )
                    )
                }
            }

        }
    }

    private fun addTask(){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}