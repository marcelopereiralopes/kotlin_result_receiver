package com.example.marcelolopes.bankdroid

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver


class BankService: IntentService("") {

    private enum class Actions {
        BALANCE, DEPOSIT, WITHDRAW
    }

    private enum class PARAM {
        AMOUNT, RESULT_RECEIVER
    }


    override fun onHandleIntent(intent: Intent?) {
        val resultReceiver: ResultReceiver = intent?.getParcelableExtra(PARAM.RESULT_RECEIVER.name)!!

        val action = intent.action

        when (action) {
            Actions.BALANCE.name -> handleRetrieveBalance(resultReceiver)
            Actions.DEPOSIT.name -> {
                val amount = intent.getIntExtra(PARAM.AMOUNT.name, 0)
                handleDeposit(resultReceiver, amount)
            }
            Actions.WITHDRAW.name -> {
                val amount = intent.getIntExtra(PARAM.AMOUNT.name, 0)
                handleWithdraw(resultReceiver, amount);
            }
        }
    }

    private fun sleep(i: Int) {
        try {
            Thread.sleep(i.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    private fun handleWithdraw(resultReceiver: ResultReceiver?, amount: Int) {
        val bundle = Bundle()
        val code: Int

        //Just add sleep to simulate network latency
        sleep(1000)

        if (balance < amount) {
            code = BankResultReceiver.RESULT_CODE_ERROR
            bundle.putSerializable(BankResultReceiver.PARAM_EXCEPTION, FunctionalException("Not enough credit"))
        } else {
            code = BankResultReceiver.RESULT_CODE_OK
            balance -= amount
            bundle.putSerializable(BankResultReceiver.PARAM_RESULT, true)
        }
        resultReceiver?.send(code, bundle)
    }

    private fun handleDeposit(resultReceiver: ResultReceiver?, amount: Int) {
        val bundle = Bundle()
        val code: Int
        //Just add sleep to simulate network latency
        sleep(1000)
        if (amount < 0) {
            code = BankResultReceiver.RESULT_CODE_ERROR
            bundle.putSerializable(BankResultReceiver.PARAM_EXCEPTION, FunctionalException("Negative amount"))
        } else {
            code = BankResultReceiver.RESULT_CODE_OK
            balance += amount
            bundle.putSerializable(BankResultReceiver.PARAM_RESULT, true)
        }
        resultReceiver?.send(code, bundle)

    }

    private fun handleRetrieveBalance(resultReceiver: ResultReceiver?) {
        val bundle = Bundle()
        val code = BankResultReceiver.RESULT_CODE_OK
        //Just add sleep to simulate network latency
        sleep(500)
        bundle.putSerializable(BankResultReceiver.PARAM_RESULT, balance)
        resultReceiver?.send(code, bundle)

    }

    companion object {

        private var balance = 10

        fun startServiceForBalance(context: Context, resultReceiverCallBack: BankResultReceiver.ResultReceiverCallback<Int>) {
            val bankResultReceiver = BankResultReceiver<Int>(Handler(context.mainLooper))
            bankResultReceiver.setReceiver(resultReceiverCallBack)

            val intent = Intent(context, BankService::class.java)
            intent.action = Actions.BALANCE.name
            intent.putExtra(PARAM.RESULT_RECEIVER.name, bankResultReceiver)
            context.startService(intent)
        }

        fun startServiceToDeposit(context: Context, amount: Int, resultReceiverCallBack: BankResultReceiver.ResultReceiverCallback<Int>) {
            val bankResultReceiver = BankResultReceiver<Int>(Handler(context.mainLooper))
            bankResultReceiver.setReceiver(resultReceiverCallBack)

            val intent = Intent(context, BankService::class.java)
            intent.action = Actions.DEPOSIT.name
            intent.putExtra(PARAM.AMOUNT.name, amount)
            intent.putExtra(PARAM.RESULT_RECEIVER.name, bankResultReceiver)
            context.startService(intent)
        }

        fun startServiceToWithdraw(context: Context, amount: Int, resultReceiverCallBack: BankResultReceiver.ResultReceiverCallback<Int>) {
            val bankResultReceiver = BankResultReceiver<Int>(Handler(context.mainLooper))
            bankResultReceiver.setReceiver(resultReceiverCallBack)

            val intent = Intent(context, BankService::class.java)
            intent.action = Actions.WITHDRAW.name
            intent.putExtra(PARAM.AMOUNT.name, amount)
            intent.putExtra(PARAM.RESULT_RECEIVER.name, bankResultReceiver)
            context.startService(intent)
        }

    }



}