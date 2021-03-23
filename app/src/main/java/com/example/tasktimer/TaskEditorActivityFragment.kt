package com.example.tasktimer

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class TaskEditorActivityFragment : Fragment() {

    private lateinit var name: EditText
    private lateinit var description: EditText
    private lateinit var sortOrder: EditText
    private lateinit var save: Button
    private var fragmentMode: FragmentEditMode = FragmentEditMode.ADD
    companion object {
        private const val TAG = "AddEditActivityFragment"
    }
    private enum class FragmentEditMode { EDIT, ADD}



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: starts")
        val view = inflater.inflate(R.layout.fragment_add_edit, container, false)
        name = view.findViewById(R.id.et_addtasks_name)
        description = view.findViewById(R.id.et_addtasks_description)
        sortOrder = view.findViewById(R.id.et_addtasks_sortorder)
        save = view.findViewById(R.id.btn_addtasks_save)
        val args = activity?.intent?.extras
        Log.d(TAG, "onCreateView: $args")
        val sortOrderValue = if (sortOrder.length() > 0) {
            sortOrder.text.toString().toInt()
        } else {
            0
        }

        val task = editTask(args)

        save.setOnClickListener {
            val contentResolver = activity?.contentResolver
            val values = ContentValues()
            when(fragmentMode) {
                FragmentEditMode.ADD -> {
                    if (name.length() > 0) {
                        Log.d(TAG, "onCreateView: new task")
                        values.put(TaskContract.Columns.TASKS_NAME, name.text.toString())
                        values.put(TaskContract.Columns.TASKS_DESCRIPTION, description.text.toString())
                        values.put(TaskContract.Columns.TASKS_SORT_ORDER, sortOrderValue)
                        contentResolver?.insert(TaskContract.CONTENT_URI, values)
                    }
                }
                FragmentEditMode.EDIT -> {
                    if (name.text.toString() != task?.name) { values.put(TaskContract.Columns.TASKS_NAME, name.text.toString()) }
                    if (description.text.toString() != task?.description) { values.put(TaskContract.Columns.TASKS_DESCRIPTION, description.text.toString()) }
                    if (sortOrderValue != task?.sortOrder) { values.put(TaskContract.Columns.TASKS_SORT_ORDER, sortOrderValue) }
                    if (values.size() != 0) {
                        Log.d(TAG, "onCreateView: updating task")
                        contentResolver?.update(TaskContract.UriBuilder.buildTaskUri(task?._id!!), values, null, null)
                    }
                }
            }
        }
        Log.d(TAG, "onCreateView: R.I.P.")
        return view
    }

    private fun editTask(args: Bundle?) : Task? {
        var task: Task? = null
        Log.d(TAG, "editTask: $args")
        if (args != null) {
            Log.d(TAG, "onCreateView: retrieving task details")
            task = args.getSerializable(Task::class.java.simpleName) as Task?
            fragmentMode = if (task != null) {
                Log.d(TAG, "onCreateView: Task details found, editing")
                name.setText(task.name)
                description.setText(task.description)
                sortOrder.setText(task.sortOrder.toString())
                FragmentEditMode.EDIT
            } else {
                FragmentEditMode.ADD
            }
        } else {
            FragmentEditMode.ADD
        }
        return task
    }
}

