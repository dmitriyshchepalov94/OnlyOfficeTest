package com.example.testtask.viewModel

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testtask.BR
import com.example.testtask.View.LoginActivity
import com.example.testtask.Model.BaseFileClass
import com.example.testtask.Model.File
import com.example.testtask.Model.Folder
import com.example.testtask.Model.User
import com.example.testtask.R
import com.example.testtask.Util.RequestMaker
import com.example.testtask.databinding.DocsListItemBinding
import com.example.testtask.databinding.NavigationHeaderBinding
import com.squareup.picasso.Picasso


class DocsViewModel(val context: Context, var mUser: User, val headerBinding: NavigationHeaderBinding? = null): BaseObservable(), LifecycleObserver {

    var mFile: BaseFileClass? = null
    var mAdapterItem = ItemAdapter(mutableListOf<BaseFileClass>())
    var mRequestMaker: RequestMaker? = null
    var mFolder = Folder()
    var mLoadingProcess = false
    fun getFileName():String
    {
        return mFile?.title!!
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun createFragment()
    {
        val docsWatcherHandler = Handler()
        mRequestMaker = mUser.let { RequestMaker(it, docsWatcherHandler) }
        mRequestMaker?.setNetworkInterfaceListener(
            object : RequestMaker.NetworkInterface {
                override fun onAuthenticationSuccess() {

                }

                override fun onAuthenticationFail() {

                }

                override fun onGetUserInfo() {
                    headerBinding?.headerViewModel?.updateInfo(mUser, context)
                }

                override fun onGetUserDocs() {
                    mAdapterItem = mUser.mFiles.let { ItemAdapter(it) }
                    mAdapterItem.notifyDataSetChanged()
                    mLoadingProcess = false
                    notifyPropertyChanged(BR._all)
                }

                override fun onUserDocsFailure() {
                    Toast.makeText(context, context.resources.getString(R.string.docs_error), Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onUserInfoFailure() {
                    Toast.makeText(context, context.resources.getString(R.string.info_error), Toast.LENGTH_SHORT).show()
                }
            }
        )
        mRequestMaker?.start()
        mRequestMaker?.looper
        Thread.sleep(100)
        mLoadingProcess = true
        notifyPropertyChanged(BR._all)
        mRequestMaker?.sendMessage(RequestMaker.MESSAGE_USER_INFO)
        mRequestMaker?.sendMessage(RequestMaker.MESSAGE_USER_DOCS)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy()
    {
        mUser.mFiles.clear()
        mLoadingProcess = false
    }

    fun logOut()
    {
        val intent = LoginActivity.newIntent(context)
        context.startActivity(intent)
    }


    fun onFolderBack()
    {
        mUser.mFiles.clear()
        if(!mFolder.path.isEmpty()) {
            mRequestMaker?.sendMessage(RequestMaker.MESSAGE_PARENT_FOLDER, mFolder)
        }
    }

    fun onNavigationItemClick(item: MenuItem)
    {
        when(item.itemId)
        {
            R.id.my_docs ->
            {
                mUser.mFiles.clear()
                mRequestMaker?.sendMessage(RequestMaker.MESSAGE_USER_DOCS)
            }
            R.id.common_docs ->
            {
                mUser.mFiles.clear()
                mRequestMaker?.sendMessage(RequestMaker.MESSAGE_COMMON_DOCS)
            }
        }
    }

    inner class ItemViewHolder(val binding: DocsListItemBinding): RecyclerView.ViewHolder(binding.root)
    {
        init {
            binding.listItemViewModel = context?.let { mUser?.let { it1 -> DocsViewModel(it, it1) } }
        }
        fun bind(file: BaseFileClass?)
        {
            binding.listItemViewModel?.mFile = file
            binding.executePendingBindings()
        }
    }

    inner class ItemAdapter(val mutableList: MutableList<BaseFileClass>): RecyclerView.Adapter<ItemViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val inflater = LayoutInflater.from(context)
            val binding = DataBindingUtil.inflate<DocsListItemBinding>(inflater,
                R.layout.docs_list_item, parent, false)
            return ItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = mutableList.get(position)
            if(item is Folder) {
                holder.itemView.setOnClickListener {
                    mFolder = item
                    mUser.mFiles.clear()
                    mRequestMaker?.sendMessage(RequestMaker.MESSAGE_FOLDER_CONTENT, mFolder)
                }
            }
            holder.bind(item)
        }

        override fun getItemCount(): Int {
                return mutableList.size
        }

    }

    object DocsWatcherBindingAdapter{
        @BindingAdapter("app:setAdapter")
        @JvmStatic
        fun setAdapter(view: RecyclerView, itemAdapter: ItemAdapter)
        {
            view.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = itemAdapter
            }
        }
        @BindingAdapter("app:setText")
        @JvmStatic
        fun setText(view: TextView, string: String)
        {
            view.text = string
        }

        @BindingAdapter("app:setImage")
        @JvmStatic
        fun setImage(view: ImageView, file: BaseFileClass)
        {
            if(file is File)
            {
                Picasso.get().load(R.drawable.ic_doc).fit().centerCrop().into(view)
            }
            else if(file is Folder)
            {
                Picasso.get().load(R.drawable.ic_folder).fit().centerCrop().into(view)
            }
        }
        @BindingAdapter("app:setVisible")
        @JvmStatic
        fun setVisible(view: ProgressBar, visibility: Boolean)
        {
            if(visibility) view.visibility = View.VISIBLE else view.visibility = View.INVISIBLE
        }
    }

}