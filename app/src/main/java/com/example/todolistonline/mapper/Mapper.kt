package com.example.todolistonline.mapper

import android.content.Context
import android.util.TypedValue
import com.example.todolistonline.domain.Task
import com.example.todolistonline.data.entities.TaskDbModel
import javax.inject.Inject

class Mapper @Inject constructor(
    private val context: Context
) {

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    fun entityToDbModel(task: Task): TaskDbModel {
        return TaskDbModel(
            task.id,
            task.title,
            task.priority,
            task.time,
            task.state
        )
    }

    fun dbModelToEntity(task: TaskDbModel):Task{
        return Task(
            task.id,
            task.title,
            task.priority,
            task.time,
            task.state
        )
    }






}