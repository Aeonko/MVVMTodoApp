package com.nemanjamiseljic.mvvmtodoapp.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nemanjamiseljic.mvvmtodoapp.data.Task
import com.nemanjamiseljic.mvvmtodoapp.databinding.ItemTaskBinding

class TasksAdapter: ListAdapter<Task, TasksAdapter.TasksViewHolder>(DiffCallback()) {
    /**List adapter is type of recyclerview adapter that is used
     * ... when data changes whole list at once not just one item or part of items
     * ... In our case we get new list every time we get new data soo it makes sense to use ListAdapter instead of ReyclerViewAdapter
     * ... Recyclerview adapter is more suited to be used when data changes one or more items in list of already loaded items**/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    class TasksViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root){
        /**Binding data from item_task.xml to view holder**/

        fun bind(task: Task){
            binding.apply {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) = /**Returns true/false if old item equals new item**/
            oldItem == newItem
    }
}