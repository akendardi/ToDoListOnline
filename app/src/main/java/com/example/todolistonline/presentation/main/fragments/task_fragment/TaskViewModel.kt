package com.example.todolistonline.presentation.main.fragments.task_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistonline.domain.Task
import com.example.todolistonline.domain.TaskRepository
import com.example.todolistonline.mapper.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repository: TaskRepository

    @Inject
    lateinit var mapper: Mapper

    private val _tasksList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>>
        get() = _tasksList


    fun addNewTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val taskDb = mapper.entityToDbModel(task)
                repository.insertTask(taskDb)
            }
        }
    }

    fun dpToPx(dp: Int): Int{
        return mapper.dpToPx(dp)
    }
}