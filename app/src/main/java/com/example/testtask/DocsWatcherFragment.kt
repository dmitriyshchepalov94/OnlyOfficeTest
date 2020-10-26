package com.example.testtask

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.testtask.databinding.DocsWatcherFragmentBinding
import com.example.testtask.databinding.NavigationHeaderBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.docs_watcher_fragment.*

const val USER = "USER"

class DocsWatcherFragment: Fragment(), NavigationView.OnNavigationItemSelectedListener {

    var mUser: User? = null
    var mDocsWatcherBinding: DocsWatcherFragmentBinding? = null
    companion object
    {
        fun newInstance(user:User):DocsWatcherFragment {
            val args = Bundle()

            args.putParcelable(USER, user)
            val fragment = DocsWatcherFragment()
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        mDocsWatcherBinding  = DataBindingUtil.inflate<DocsWatcherFragmentBinding>(inflater, R.layout.docs_watcher_fragment, container, false)

        mUser = arguments?.getParcelable<User>(USER)


        val navigationHeader: NavigationHeaderBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.navigation_header, mDocsWatcherBinding?.watcherNavigationView, false)
        mDocsWatcherBinding?.watcherNavigationView?.addHeaderView(navigationHeader.root)
        navigationHeader.headerViewModel = HeaderViewModel(mUser!!)
        mDocsWatcherBinding?.recyclerViewModel = context?.let { DocsViewModel(it, mUser!!, navigationHeader) }
        return mDocsWatcherBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        watcher_navigation_view.setNavigationItemSelectedListener(this)
        (activity as AppCompatActivity).setSupportActionBar(watcher_toolbar)
        setHasOptionsMenu(true)

        watcher_navigation_view.menu.getItem(0).setChecked(true)
        val burgerButton = ActionBarDrawerToggle(activity, watcher_drawer, watcher_toolbar, R.string.open, R.string.close)
        watcher_drawer.addDrawerListener(burgerButton)
        burgerButton.syncState()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.docs_watcher_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.on_folder_back ->
            {
                mDocsWatcherBinding?.recyclerViewModel?.onFolderBack()
                return true
            }
            else -> { return super.onOptionsItemSelected(item) }
        }

    }

    override fun onStart() {
        super.onStart()
        lifecycle.addObserver(mDocsWatcherBinding?.recyclerViewModel!!)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mDocsWatcherBinding?.recyclerViewModel?.onNavigationItemClick(item)
        watcher_drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.addObserver(mDocsWatcherBinding?.recyclerViewModel!!)
        retainInstance = true
    }
}