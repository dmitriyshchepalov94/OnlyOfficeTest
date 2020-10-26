package com.example.testtask.View

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.testtask.R

abstract class SingleFragmentActivity: AppCompatActivity() {
    @LayoutRes
    protected open fun getLayoutResId(): Int
    {
        return R.layout.single_fragment_activity
    }

    abstract fun createFragment(): Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        val fm: FragmentManager = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragment_container)
        if(fragment == null)
        {
            fragment = createFragment()
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }
}