package com.all.media.kotlin

import Models.ResultModel

interface KittyFileSelectionListener {
    fun onKittySelection(result : ArrayList<ResultModel>)
}