package com.nemanjamiseljic.mvvmtodoapp.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nemanjamiseljic.mvvmtodoapp.R
import com.nemanjamiseljic.mvvmtodoapp.databinding.FragmentTaskBinding
import com.nemanjamiseljic.mvvmtodoapp.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TasksFragment: Fragment(R.layout.fragment_task) {

    /**Gets instance of viewmodel**/
    private val viewModel: TasksViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTaskBinding.bind(view)

        val tasksAdapter = TasksAdapter()


        binding.apply {
            recyclerViewTasks.apply {
                adapter = tasksAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner,{
            tasksAdapter.submitList(it)
        })
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_task,menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView  /**Finds search view in inflated menu**/

        searchView.onQueryTextChanged {
            //update search query
            viewModel.searchQuery.value = it
        }  /**this onQueryTextChanged is custom written function
                                            *...It can be found in our class in mvvmtodoapp/util/ViewExt.kt **/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_sort_by_name->{

                true
            }
            R.id.action_sort_by_date_created->{
                true
            }
            R.id.action_hide_completed_tasks->{
                true
            }
            R.id.action_hide_completed_tasks->{
                item.isChecked = !item.isChecked
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