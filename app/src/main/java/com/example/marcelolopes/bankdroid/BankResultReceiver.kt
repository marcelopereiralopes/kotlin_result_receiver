package com.example.marcelolopes.bankdroid

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import java.io.Serializable

open class BankResultReceiver<T>(handler: Handler?) : ResultReceiver(handler) {

    private lateinit var receiverCallback: ResultReceiverCallback<T>

    companion object {
        val RESULT_CODE_OK = 1100
        val RESULT_CODE_ERROR = 555
        val PARAM_RESULT = "result"
        val PARAM_EXCEPTION = "exception"
    }


    fun setReceiver(resultReceiverCallback: ResultReceiverCallback<T>) {
        receiverCallback = resultReceiverCallback
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        super.onReceiveResult(resultCode, resultData)

        if (resultCode == RESULT_CODE_OK) {
            receiverCallback.onSuccess(resultData!!.getSerializable(PARAM_RESULT))
        } else {
            receiverCallback.onError(resultData!!.getSerializable(PARAM_EXCEPTION) as Exception)
        }
    }

    interface ResultReceiverCallback<T> {
        fun onSuccess(data: Serializable)
        fun onError(e: Exception)
    }
}