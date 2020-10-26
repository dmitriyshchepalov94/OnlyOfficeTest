package com.example.testtask

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User() : Parcelable {
    @SerializedName("id")
    @Expose
    var mId = ""
    @SerializedName("userName")
    @Expose
    var mUserName = ""
    @SerializedName("password")
    @Expose
    var mPassword = ""
    var mAccessToken = ""
    var mFirstName = ""
    var mLastName = ""
    var mEmail = ""
    var mSmallAvatarURL = ""
    var mPortal = ""
    var mFiles = mutableListOf<BaseFileClass>()

    constructor(parcel: Parcel) : this() {
        mUserName = parcel.readString()!!
        mPassword = parcel.readString()!!
        mAccessToken = parcel.readString()!!
        mFirstName = parcel.readString()!!
        mLastName = parcel.readString()!!
        mEmail = parcel.readString()!!
        mSmallAvatarURL = parcel.readString()!!
        mId = parcel.readString()!!
        mPortal = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mUserName)
        parcel.writeString(mPassword)
        parcel.writeString(mAccessToken)
        parcel.writeString(mFirstName)
        parcel.writeString(mLastName)
        parcel.writeString(mEmail)
        parcel.writeString(mSmallAvatarURL)
        parcel.writeString(mId)
        parcel.writeString(mPortal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}