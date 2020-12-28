package com.nemanjamiseljic.mvvmtodoapp.util

import androidx.appcompat.widget.SearchView

inline fun SearchView.onQueryTextChanged(crossinline  lisener: (String) -> Unit){
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            lisener(newText.orEmpty())
            return true
        }
    })
}