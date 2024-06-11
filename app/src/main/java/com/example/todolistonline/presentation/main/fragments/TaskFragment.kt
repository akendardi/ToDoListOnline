package com.example.todolistonline.presentation.main.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.todolistonline.R
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.databinding.FragmentTaskBinding
import com.example.todolistonline.domain.Task
import com.example.todolistonline.domain.TaskState
import com.example.todolistonline.presentation.ViewModelFactory
import com.example.todolistonline.presentation.main.MainViewModel
import javax.inject.Inject


class TaskFragment : DialogFragment() {

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    private val viewModel: MainViewModel by activityViewModels { viewModelFactory }

    private var task: Task? = null
    private var day: Int = 0


    private val component by lazy {
        (activity?.application as ToDoListOnlineApp).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_App_Dialog)
        getParams()
    }

    private fun getParams(){
        arguments?.let {
            arguments?.let {
                task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.getParcelable(ARG_TASK, Task::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    it.getParcelable(ARG_TASK) as? Task
                }
                day = it.getInt(ARG_DAY)
            }
        }
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
        setInfo()
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
            if (editTextControl()) {
                viewModel.addNewTask(createTask())
                dismiss()
            }
        }
    }

    private fun setInfo(){
        if (task != null) {
            binding.etTask.setText(task?.title ?: "")
            when (task?.priority) {
                0 -> binding.radioGroup.check(R.id.rb_easy)
                1 -> binding.radioGroup.check(R.id.rb_medium)
                2 -> binding.radioGroup.check(R.id.rb_hard)
            }
        }
    }

    private fun editTextControl(): Boolean {
        var flag = true
        if (binding.etTask.text.toString().isEmpty()) {
            binding.etTaskL.layoutParams.height = viewModel.dpToPx(80)
            binding.etTaskL.error = "Задание не должно быть пустым"
            flag = false
        }
        if (binding.radioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(context, "Вы не выбрали уровень приоритета", Toast.LENGTH_SHORT).show()
            flag = false
        }
        return flag
    }


    private fun createTask(): Task {

        val id = task?.id?:0
        val title = binding.etTask.text.toString()
        val checkedRadioButtonId = binding.radioGroup.checkedRadioButtonId
        val radioButton = binding.radioGroup.findViewById<RadioButton>(checkedRadioButtonId)
        val priority = when (radioButton.text.toString()) {
            "Низкий" -> 0
            "Средний" -> 1
            "Высокий" -> 2
            else -> 0
        }
        val state = TaskState.None
        return Task(
            id,
            title,
            priority,
            day,
            state
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val ARG_TASK = "arg_task"
        private const val ARG_DAY = "day"

        fun newInstance(task: Task? = null, day: Int): TaskFragment {
            val fragment = TaskFragment()
            val args = Bundle().apply {
                putParcelable(ARG_TASK, task)
                putInt(ARG_DAY, day)
            }
            fragment.arguments = args
            return fragment
        }

        fun newInstance(task: Task? = null): TaskFragment {
            val fragment = TaskFragment()
            val args = Bundle().apply {
                putParcelable(ARG_TASK, task)
            }
            fragment.arguments = args
            return fragment
        }
    }
}