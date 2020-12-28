package com.nemanjamiseljic.mvvmtodoapp.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nemanjamiseljic.mvvmtodoapp.data.Task
import com.nemanjamiseljic.mvvmtodoapp.databinding.ItemTaskBinding

class TasksAdapter(private val listener: OnItemClickListener): ListAdapter<Task, TasksAdapter.TasksViewHolder>(DiffCallback()) {
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


    /**Marked as inner class for tight coupling of adapter and ViewHolder
     * ...This way we can access things like getItem() used to get current item when clicked on item**/
   inner class TasksViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root){
        /**Binding data from item_task.xml to view holder**/

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        /**When item is deleted its animated
                         * ...and while it is animated its still posiblle to click on him
                         * ...this way we make sure we can't click on him while its animated
                         * ...RecyclerView.NO_POSITION is constant for -1 and this way we check if item still exits**/

                        val task = getItem(position)
                        listener.onItemClick(task)
                    }
                }
                checkBoxCompleted.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val task = getItem(position)
                        listener.onCheckBoxClick(task,checkBoxCompleted.isChecked)
                    }
                }
            }
        }


        fun bind(task: Task){  /**Binding data to view R.layout.fragment_task**/
            binding.apply {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(task: Task)
        fun onCheckBoxClick(task: Task,isChecked: Boolean)
    }


    class DiffCallback: DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) = /**Returns true/false if old item equals new item**/
            oldItem == newItem
    }
}