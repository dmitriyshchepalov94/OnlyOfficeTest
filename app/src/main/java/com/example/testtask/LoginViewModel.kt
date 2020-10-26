package com.example.testtask

import android.content.Context
import android.util.Patterns
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleObserver


class LoginViewModel(val context: Context, var mUser: User): LifecycleObserver, BaseObservable() {

    var mIsAuthenticationProcess = false

    object LoginBindingAdapter{
        @BindingAdapter("app:setVisible")
        @JvmStatic
        fun setVisible(view: FrameLayout, bool: Boolean)
        {
            if(bool) view.visibility = View.VISIBLE else view.visibility = View.INVISIBLE
        }
    }



    fun onLoginButtonClick() {
        if(validate()) {
            startAuthentication()
        }
    }

    fun getUser(): User
    {
        return mUser
    }

    fun afterLoginChange(login: CharSequence) {
        mUser?.mUserName = login.toString().trim()
    }

    fun afterPasswordChange(password: CharSequence) {
        mUser?.mPassword = password.toString().trim()
    }

    fun afterPortalChanged(portal: CharSequence) {
        mUser?.mPortal = portal.toString().trim()
    }

    private fun validate(): Boolean
    {
        if(mUser?.mUserName?.isEmpty()!!)
        {
            Toast.makeText(context, context.resources.getString(R.string.login_error_empty), Toast.LENGTH_SHORT).show()
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(mUser?.mUserName).matches())
        {
            Toast.makeText(context, context.resources.getString(R.string.login_error), Toast.LENGTH_SHORT).show()
            return false
        }
        if(mUser?.mPassword?.isEmpty()!!)
        {
            Toast.makeText(context, context.resources.getString(R.string.password_error), Toast.LENGTH_SHORT).show()
            return false
        }
        if(mUser?.mPortal?.isEmpty()!!)
        {
            Toast.makeText(context, context.resources.getString(R.string.portal_error), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun startAuthentication()
    {
        val request = RequestMaker(mUser!!, null)
        request.setNetworkInterfaceListener(
            object : RequestMaker.NetworkInterface {
                override fun onAuthenticationSuccess() {
                    val intent = DocsWatcherActivity.newIntent(context, mUser!!)
                    notifyPropertyChanged(BR._all)
                    mIsAuthenticationProcess = false
                    context.startActivity(intent)
                }

                override fun onAuthenticationFail() {
                    Toast.makeText(context, context.resources.getString(R.string.auth_error), Toast.LENGTH_SHORT).show()
                    mIsAuthenticationProcess = false
                    notifyPropertyChanged(BR._all)
                }

                override fun onGetUserInfo() {

                }

                override fun onGetUserDocs() {

                }

                override fun onUserDocsFailure() {

                }

                override fun onUserInfoFailure() {

                }


            }
        )
        request.start()
        request.looper
        mIsAuthenticationProcess = true
        Thread.sleep(100)
        notifyPropertyChanged(BR._all)
        request.sendMessage(RequestMaker.MESSAGE_AUTHENTICATION)
    }
}