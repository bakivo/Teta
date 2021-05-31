package com.example.teta.utils

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Suspending wrapper around an OkHttp [Call], using [Call.enqueue].
 */
suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
    enqueue(
        object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response) {
                    // If we have a response but we're cancelled while resuming, we need to
                    // close() the unused response
                    if (response.body != null) {
                        response.closeQuietly()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }
        }
    )

    continuation.invokeOnCancellation {
        try {
            cancel()
        } catch (t: Throwable) {
            // Ignore cancel exception
        }
    }
}
/**
 * Suspending wrapper around an OkHttp [Call], using [Call.execute].
 */
suspend fun Call.await2(): Response = suspendCancellableCoroutine {

    try {
        it.resume(this.execute())
    } catch (t: Throwable) {
        println(t.message)
        it.resumeWithException(t)
    }

    it.invokeOnCancellation {
        try {
            cancel()
        } catch (t:Throwable) {
            // Ignore cancel exception
        }
    }
}