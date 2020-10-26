package com.example.testtask.Util

import com.example.testtask.Model.File
import com.example.testtask.Model.Folder
import com.example.testtask.Model.User
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlin.math.roundToInt

class JsonParser(var mUser: User) {

    private fun getResponseMap(json: JsonObject): Map<String, String>
    {
        val responseMap: Map<String, String> = Gson().fromJson(json, Map::class.java) as Map<String, String>
        return responseMap.get(JsonParserSchema.JSON_RESPONSE) as Map<String, String>
    }


    fun parseUserInfo(json: JsonObject)
    {
        val responseMap = getResponseMap(json)
        mUser.apply {
            mFirstName = responseMap.get(JsonParserSchema.JSON_FIRSTNAME).toString()
            mLastName = responseMap.get(JsonParserSchema.JSON_LASTNAME).toString()
            mEmail = responseMap.get(JsonParserSchema.JSON_EMAIL).toString()
            mSmallAvatarURL = responseMap.get(JsonParserSchema.JSON_AVATAR).toString()
            mId = responseMap.get(JsonParserSchema.JSON_ID).toString()
        }
    }
    fun parseToken(json: JsonObject): Boolean
    {
        val responseMap = getResponseMap(json)
        mUser.mAccessToken = responseMap.get(JsonParserSchema.JSON_TOKEN).toString()
        return !mUser.mAccessToken.isEmpty()
    }

    fun parseFiles(json: JsonObject)
    {
        val responseMap = getResponseMap(json)
        val listOfFiles = responseMap.get(JsonParserSchema.JSON_FILES) as List<String>
        var length = 0
        while(length < listOfFiles.size)
        {
            val mapOfFiles = listOfFiles.get(length) as Map<String, Double>
            val file = File()
            file.apply {
                id = mapOfFiles.get(JsonParserSchema.JSON_ID)?.roundToInt().toString()
                title = mapOfFiles.get(JsonParserSchema.JSON_TITLE).toString()
            }
            mUser.mFiles.add(file)
            length++
        }
        length = 0
        val listOfFolders = responseMap.get(JsonParserSchema.JSON_FOLDERS) as List<String>
        val listOfIds = responseMap.get(JsonParserSchema.JSON_PATHPARTS) as List<Double>
        while (length < listOfFolders.size)
        {
            val mapOfFolders = listOfFolders.get(length) as Map<String, Double>
            val folder = Folder()
            folder.apply {
                id = mapOfFolders.get(JsonParserSchema.JSON_ID)?.roundToInt().toString()
                title = mapOfFolders.get(JsonParserSchema.JSON_TITLE).toString()
                listOfIds.forEach { path.add(it.roundToInt().toString())}
            }
            mUser.mFiles.add(folder)
            length++
        }


    }

}