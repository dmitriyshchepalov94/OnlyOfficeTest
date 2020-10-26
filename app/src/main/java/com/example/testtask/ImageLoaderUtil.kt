package com.example.testtask

import android.graphics.*
import com.squareup.picasso.Transformation
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class ImageLoaderUtil: Transformation {

        companion object {
            private val KEY = "circleImage"

            fun getCustomImageLoader(user: User): OkHttpClient
            {
                val client = OkHttpClient.Builder().addInterceptor(
                    object : Interceptor
                    {
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val request = chain.request().newBuilder()
                                .addHeader("${NetworkServiceHeaders.HEADER_AUTORIZATION}", "${user.mAccessToken}")
                                .build()
                            return chain.proceed(request)
                        }

                    }
                ).build()
                return client
            }

        }

        override fun transform(source: Bitmap): Bitmap {

            val minEdge = Math.min(source.width, source.height)
            val dx = (source.width - minEdge) / 2
            val dy = (source.height - minEdge) / 2

            val shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            val matrix = Matrix()
            matrix.setTranslate((-dx).toFloat(), (-dy).toFloat())
            shader.setLocalMatrix(matrix)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.shader = shader

            val output = Bitmap.createBitmap(minEdge, minEdge, source.config)
            val canvas = Canvas(output)
            canvas.drawOval(RectF(0f, 0f, minEdge.toFloat(), minEdge.toFloat()), paint)

            source.recycle()

            return output
        }

        override fun key(): String = KEY
}