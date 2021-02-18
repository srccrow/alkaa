package com.escodro.task.presentation.fake

import com.escodro.domain.model.Category
import com.escodro.domain.model.Task
import com.escodro.domain.model.TaskWithCategory
import com.escodro.domain.usecase.taskwithcategory.LoadUncompletedTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.Calendar

internal class LoadUncompletedTasksFake : LoadUncompletedTasks {

    var list: List<TaskWithCategory> = listOf()

    var throwError: Boolean = false

    fun returnDefaultValues() {
        val task1 = Task(title = "Buy milk", dueDate = null)
        val task2 = Task(title = "Call Mark", dueDate = Calendar.getInstance())
        val task3 = Task(title = "Watch Moonlight", dueDate = Calendar.getInstance())

        val category1 = Category(name = "Books", color = "#FF0000")
        val category2 = Category(name = "Reminders", color = "#00FF00")

        val taskList = listOf(
            TaskWithCategory(task = task1, category = category1),
            TaskWithCategory(task = task2, category = category2),
            TaskWithCategory(task = task3, category = null)
        )

        list = taskList
    }

    fun returnValues(numberOfValues: Int) {
        val task = Task(title = "Buy milk", dueDate = null)
        val category = Category(name = "Books", color = "#FF0000")

        val taskList = mutableListOf<TaskWithCategory>()
        for (i in 1..numberOfValues) {
            taskList.add(TaskWithCategory(task = task.copy(id = i.toLong()), category = category))
        }

        list = taskList
    }

    fun clean() {
        list = listOf()
    }

    override fun invoke(): Flow<List<TaskWithCategory>> {
        return if (throwError.not()) {
            flowOf(list)
        } else {
            flowOf(list).map { throw IllegalStateException() }
        }
    }
}