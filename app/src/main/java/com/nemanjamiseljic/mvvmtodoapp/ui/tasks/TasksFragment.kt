package com.nemanjamiseljic.mvvmtodoapp.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nemanjamiseljic.mvvmtodoapp.R
import com.nemanjamiseljic.mvvmtodoapp.data.SortOrder
import com.nemanjamiseljic.mvvmtodoapp.data.Task
import com.nemanjamiseljic.mvvmtodoapp.databinding.FragmentTaskBinding
import com.nemanjamiseljic.mvvmtodoapp.util.exchaustive
import com.nemanjamiseljic.mvvmtodoapp.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TasksFragment: Fragment(R.layout.fragment_task), TasksAdapter.OnItemClickListener {

    /**Gets instance of viewmodel**/
    private val viewModel: TasksViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTaskBinding.bind(view)

        val tasksAdapter = TasksAdapter(this)


        binding.apply {
            recyclerViewTasks.apply {
                adapter = tasksAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = tasksAdapter.currentList[viewHolder.adapterPosition] //Gets current item in recyclerView
                    viewModel.onTaskSwiped(task)
                }
            }).attachToRecyclerView(recyclerViewTasks)

            fabAddTask.setOnClickListener {
                viewModel.onAddNewTaskClick()
            }
        }




        viewModel.tasks.observe(viewLifecycleOwner,{
            tasksAdapter.submitList(it)
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            /**
             * ...launchWhenStarted
             * ...Makes sure that this coroutine can't be called when this fragment is not shown**/

            /**We collect channels in coroutines because we don't want to block main UI thread with channels**/

            viewModel.tasksEvent.collect {event->
                /**
                 * ...COLLECT passing us stream of values for taskEvent
                 * ...when data is changed we start code bellow**/

                when(event){
                    is TasksViewModel.TasksEvent.ShowUndoDeleteTaskMessage->{
                        Snackbar.make(requireView(),"Task deleted", Snackbar.LENGTH_LONG)
                                .setAction("UNDO"){
                                    viewModel.onUndoDeleteClick(event.task)
                                }.show()
                    }
                    is TasksViewModel.TasksEvent.NavigateToAddTaskScreen->{
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(null,"New Task")
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.NavigateToAddedTaskScreen -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(event.task,"Edit Task")
                        findNavController().navigate(action)
                    }
                }.exchaustive

            }
        }

        setHasOptionsMenu(true)
    }


    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task,isChecked)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_task,menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView  /**Finds search view in inflated menu**/

        searchView.onQueryTextChanged {
            //update search query
            viewModel.searchQuery.value = it //Tells view model for which data to search for
        }  /**this onQueryTextChanged is custom written function
                                            *...It can be found in our class in mvvmtodoapp/util/ViewExt.kt **/

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                    viewModel.preferencesFlow
                            .first()            /**Called first because we just want to read data once...If we wanted to observe data we would call collect**/
                            .hideCompleted


        } /** lifecycleScope lives as long as fragment lives. This way we make sure that we stop reading data after fragment is destroyed**/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_sort_by_name->{

                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created->{
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_completed_tasks->{
                item.isChecked = !item.isChecked
                viewModel.onHideCompleted(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks->{
                true
            }
            else->{
                super.onOptionsItemSelected(item)
            }
        }
    }
}