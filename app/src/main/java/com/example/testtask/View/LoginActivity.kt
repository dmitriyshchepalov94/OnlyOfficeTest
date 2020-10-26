package com.example.testtask.View

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

class LoginActivity : SingleFragmentActivity(){


    companion object
    {
        fun newIntent(context: Context):Intent
        {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            return intent
        }
    }

    override fun createFragment(): Fragment {
       return LoginFragment.newInstance()
    }

}