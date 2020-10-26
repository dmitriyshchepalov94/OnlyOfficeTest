package com.example.testtask

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

const val RECEIVE_USER = "USER"

class DocsWatcherActivity : SingleFragmentActivity() {


    companion object
    {
        fun newIntent(context: Context, user: User): Intent
        {
            val i = Intent(context, DocsWatcherActivity::class.java)
            i.putExtra(RECEIVE_USER, user)
            return i
        }
    }

    override fun createFragment(): Fragment {
        return DocsWatcherFragment.newInstance(intent.getParcelableExtra<User>(RECEIVE_USER)!!)
    }

}