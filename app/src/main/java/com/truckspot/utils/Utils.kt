package com.truckspot.utils

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog

object Utils {

     @JvmStatic
    fun dialog(context:Context,title:String?=null,message:String?,positiveText:String?="Ok",negativeText:String?=null,callback: dialogInterface?=null) {

        val alertDialog = AlertDialog.Builder(context).create()
         if(title!=null)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveText
        ) { dialog, which ->
            dialog.dismiss()
            callback?.positiveClick()
        }

        if(!negativeText.isNullOrEmpty())
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeText
        ) { dialog, which ->
            dialog.dismiss()
            callback?.negativeClick()
        }
        alertDialog.show()

//        val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
//        val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
//
//        val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
//        layoutParams.weight = 10f
//        btnPositive.layoutParams = layoutParams
//        btnNegative.layoutParams = layoutParams
    }
    interface dialogInterface {
        fun positiveClick()
        fun negativeClick()
    }

}