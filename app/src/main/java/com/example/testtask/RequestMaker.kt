package com.example.testtask

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Message


const val TAG = "REQUEST_MAKER"

class RequestMaker(var mUser: User, val handler: Handler?): HandlerThread(TAG) {

    companion object
    {
        const val MESSAGE_AUTHENTICATION = 0
        const val MESSAGE_USER_INFO = 1
        const val MESSAGE_USER_DOCS = 2
        const val MESSAGE_COMMON_DOCS = 3
        const val MESSAGE_USER_INFO_END = 4
        const val MESSAGE_FOLDER_CONTENT = 5

        const val MESSAGE_USER_INFO_ERROR = 6
        const val MESSAGE_USER_DOCS_ERROR = 7
        const val MESSAGE_PARENT_FOLDER = 8
        const val MESSAGE_GET_USER_DOCS = 9
    }


    private var mRequestHandler: Handler? = null
    private var mInterfaceListener: NetworkInterface? = null
    private val mRequestMaker = this

    interface NetworkInterface
    {
        fun onAuthenticationSuccess()
        fun onAuthenticationFail()
        fun onGetUserInfo()
        fun onGetUserDocs()
        fun onUserDocsFailure()
        fun onUserInfoFailure()
    }

    fun setNetworkInterfaceListener(listener: NetworkInterface)
    {
        mInterfaceListener = listener
    }

    val mNetworkService = NetworkService(mUser.mPortal)

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        mRequestHandler = @SuppressLint("HandlerLeak")
        object : Handler()
        {
            override fun handleMessage(msg: Message) {
                when(msg.what)
                {
                    MESSAGE_AUTHENTICATION -> {
                        if(mNetworkService.authentication(mUser)) mInterfaceListener?.onAuthenticationSuccess() else mInterfaceListener?.onAuthenticationFail()
                    }
                    MESSAGE_USER_INFO -> {
                        mNetworkService.getUserInfo(mUser, mRequestMaker)
                    }
                    MESSAGE_USER_DOCS -> {
                        mNetworkService?.getUserDocs(mUser, mRequestMaker)
                    }
                    MESSAGE_COMMON_DOCS -> {
                        mNetworkService?.getCommonDocs(mUser, mRequestMaker)
                    }

                    MESSAGE_FOLDER_CONTENT ->
                    {
                        mNetworkService.getFolderContent(mUser, msg.obj as Folder, mRequestMaker)
                    }
                    MESSAGE_PARENT_FOLDER ->
                    {
                        mNetworkService.getParentFolder(mUser, msg.obj as Folder, mRequestMaker)
                    }
                    MESSAGE_USER_INFO_END ->
                    {
                        handler?.post(
                            object : Runnable
                            {
                                override fun run() {
                                    mInterfaceListener?.onGetUserInfo()
                                }

                            }
                        )
                    }
                    MESSAGE_GET_USER_DOCS->
                    {
                        handler?.post(
                            object : Runnable
                            {
                                override fun run() {
                                    mInterfaceListener?.onGetUserDocs()
                                }

                            }
                        )

                    }
                    MESSAGE_USER_DOCS_ERROR ->
                    {
                        handler?.post(
                            object : Runnable
                            {
                                override fun run() {
                                    mInterfaceListener?.onUserDocsFailure()
                                }

                            }
                        )
                    }
                    MESSAGE_USER_INFO_ERROR ->
                    {
                        handler?.post(
                            object : Runnable {
                                override fun run() {
                                    mInterfaceListener?.onUserInfoFailure()
                                }

                            }
                        )
                    }
                }
            }
        }
    }


    fun sendMessage(message: Int, folder: Folder? = null)
    {
        mRequestHandler?.obtainMessage(message, folder)?.sendToTarget()
    }

}