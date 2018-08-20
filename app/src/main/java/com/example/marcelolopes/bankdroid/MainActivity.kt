package com.example.marcelolopes.bankdroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        depositButton.setOnClickListener { deposit() }

        withdrawButton.setOnClickListener { withdraw() }
        info()
    }

    private fun withdraw() {
        BankService.startServiceToWithdraw(this, 10, TransferMoneyResultReceiver(this, false))
        info()
    }

    private fun deposit() {
        BankService.startServiceToDeposit(this, 10, TransferMoneyResultReceiver(this, true))
        info()
    }

    private fun info() {
        BankService.startServiceForBalance(this, AccountInfoResultReceiver(this))

    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private inner class TransferMoneyResultReceiver(activity: MainActivity, private val deposit: Boolean) : BankResultReceiver.ResultReceiverCallback<Int> {

        private val activityRef: WeakReference<MainActivity>?

        init {
            activityRef = WeakReference(activity)
        }

        override fun onSuccess(data: Serializable) {
            if (activityRef?.get() != null) {
                activityRef.get()?.showMessage(if (deposit) "Deposited" else "Withdrew")
            }
        }

        override fun onError(e: Exception) {
            if (activityRef?.get() != null) {
                activityRef.get()?.showMessage(e.message!!)
            }
        }
    }

    private inner class AccountInfoResultReceiver(activity: MainActivity) : BankResultReceiver.ResultReceiverCallback<Int> {
        private val activityRef: WeakReference<MainActivity>?

        init {
            activityRef = WeakReference(activity)
        }

        @SuppressLint("SetTextI18n")
        override fun onSuccess(data: Serializable) {
            if (activityRef?.get() != null) {
                activityRef.get()?.label?.text = "Your balance: $data"
            }
        }

        override fun onError(e: Exception) {
            activityRef!!.get()?.showMessage("Account info failed")
        }
    }
}
