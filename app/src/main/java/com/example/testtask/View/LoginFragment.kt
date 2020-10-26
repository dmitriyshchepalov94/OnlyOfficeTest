package com.example.testtask.View


import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.testtask.Model.User
import com.example.testtask.R
import com.example.testtask.viewModel.LoginViewModel
import com.example.testtask.databinding.LoginFragmentBinding

const val SAVED_USER = "USER"
class LoginFragment: Fragment() {

    var loginBinding: LoginFragmentBinding? = null

    companion object
    {
        fun newInstance(): LoginFragment {
            val args = Bundle()

            val fragment = LoginFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var user: User? = null

        if (savedInstanceState != null)
        {
            user = savedInstanceState.getParcelable(SAVED_USER)
        }
        else
        {
            user = User()
        }

        loginBinding = DataBindingUtil.inflate<LoginFragmentBinding>(inflater,
            R.layout.login_fragment, container, false)
        loginBinding?.loginViewModel = context?.let { LoginViewModel(it, user!!) }
        return loginBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SAVED_USER, loginBinding?.loginViewModel?.getUser())
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}