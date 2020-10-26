package com.example.testtask.viewModel

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import com.example.testtask.BR
import com.example.testtask.Util.ImageLoaderUtil
import com.example.testtask.Model.User
import com.example.testtask.R
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso

class HeaderViewModel(val mUser: User): BaseObservable() {

    var mFullName = ""

    object HeaderBindingAdapter
    {
        @BindingAdapter("app:setText")
        @JvmStatic
        fun setText(view: TextView, string: String)
        {
            view.text = string
        }
    }

    fun updateInfo(user: User, context: Context)
    {
        mFullName = "${mUser.mFirstName} ${mUser.mLastName}"
        val image = (context as AppCompatActivity).findViewById<ImageView>(R.id.avatar_image_view)
        val imageLoader = ImageLoaderUtil.getCustomImageLoader(user)
        val picasso = Picasso.Builder(context).downloader(OkHttp3Downloader(imageLoader)).build()
        picasso.load("https://${user.mPortal}.onlyoffice.com/${user.mSmallAvatarURL}").transform(
            ImageLoaderUtil()
        ).fit().into(image)
        notifyPropertyChanged(BR._all)
    }

}