package com.example.testtask.Model


import com.example.testtask.Util.JsonParser
import com.example.testtask.Util.RequestMaker
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


class NetworkService(var mPortal: String){

    private var mRetrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl("https://${mPortal}.onlyoffice.com").build().create(
        requestsToApi::class.java)

    fun authentication(user: User): Boolean
    {
        val authCall = mRetrofit.authenticateUser(user)
        val requestResult = authCall.execute().body()
        requestResult?.let {
            return JsonParser(user).parseToken(it)
        } ?: return false
    }

    fun getUserInfo(user: User, requestMaker: RequestMaker)
    {
        mRetrofit.getUserInfo(getHeaderMap(user)).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    response.body()?.let { JsonParser(user).parseUserInfo(it) }
                    requestMaker.sendMessage(RequestMaker.MESSAGE_USER_INFO_END)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    requestMaker.sendMessage(RequestMaker.MESSAGE_USER_INFO_ERROR)
                }

            }
        )
    }


    fun getCommonDocs(user: User, requestMaker: RequestMaker)
    {
        mRetrofit.getCommonDocs(getHeaderMap(user)).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    response.body()?.let { JsonParser(user).parseFiles(it) }
                    requestMaker.sendMessage(RequestMaker.MESSAGE_GET_USER_DOCS)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    requestMaker.sendMessage(RequestMaker.MESSAGE_USER_DOCS_ERROR)
                }

            }
        )
    }

    fun getUserDocs(user: User, requsetMaker: RequestMaker)
    {
        mRetrofit.getUserDocs(getHeaderMap(user)).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    response.body()?.let { JsonParser(user).parseFiles(it) }
                    requsetMaker.sendMessage(RequestMaker.MESSAGE_GET_USER_DOCS)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    requsetMaker.sendMessage(RequestMaker.MESSAGE_USER_DOCS_ERROR)
                }

            }
        )

    }

    fun getFolderContent(user: User, folder: Folder, requestMaker: RequestMaker)
    {

        mRetrofit.getFolderContent(folder.id, user.mId, getHeaderMap(user)).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    response.body()?.let { JsonParser(user).parseFiles(it) }
                    requestMaker.sendMessage(RequestMaker.MESSAGE_GET_USER_DOCS)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    requestMaker.sendMessage(RequestMaker.MESSAGE_USER_DOCS_ERROR)
                }

            }
        )
    }

    fun getParentFolder(user: User, folder: Folder, requestMaker: RequestMaker)
    {
        mRetrofit.getFolderContent(folder.path[folder.path.size - 1], user.mId, getHeaderMap(user)).enqueue(
            object : Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    response.body()?.let { JsonParser(user).parseFiles(it) }
                    folder.path.removeLast()
                    requestMaker.sendMessage(RequestMaker.MESSAGE_GET_USER_DOCS)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    requestMaker.sendMessage(RequestMaker.MESSAGE_USER_DOCS_ERROR)
                }

            }
        )
    }

    private fun getHeaderMap(user: User): Map<String, String>
    {
        val headerMap = mutableMapOf<String, String>()
        headerMap[NetworkServiceHeaders.HEADER_CONTENT_TYPE] = NetworkServiceHeaders.HEADER_APP_JSON
        headerMap[NetworkServiceHeaders.HEADER_ACCEPT] = NetworkServiceHeaders.HEADER_APP_JSON
        headerMap[NetworkServiceHeaders.HEADER_AUTORIZATION] = user.mAccessToken
        return headerMap
    }


    interface requestsToApi
    {

        @Headers("${NetworkServiceHeaders.HEADER_CONTENT_TYPE}: ${NetworkServiceHeaders.HEADER_APP_JSON}", "${NetworkServiceHeaders.HEADER_ACCEPT}: ${NetworkServiceHeaders.HEADER_APP_JSON}")
        @POST("api/2.0/authentication")
        fun authenticateUser(@Body user: User): Call<JsonObject>


        @GET("api/2.0/people/@self")
        fun getUserInfo(@HeaderMap headers: Map<String, String>): Call<JsonObject>

        @GET("api/2.0/files/@common")
        fun getCommonDocs(@HeaderMap headers: Map<String, String>): Call<JsonObject>


        @GET("api/2.0/files/@my")
        fun getUserDocs(@HeaderMap headers: Map<String, String>): Call<JsonObject>

        @GET("api/2.0/files/{id}")
        fun getFolderContent(@Path("${NetworkServiceHeaders.REQUEST_ID}") id: String, @Query("${NetworkServiceHeaders.REQUEST_USER_OR_FOLDER_ID}") userId: String, @HeaderMap headers: Map<String, String>, @Query("${NetworkServiceHeaders.REQUEST_FILTER_TYPE}") filter: String = "${NetworkServiceHeaders.REQUEST_FILTER_VALUE}"):Call<JsonObject>
    }

}