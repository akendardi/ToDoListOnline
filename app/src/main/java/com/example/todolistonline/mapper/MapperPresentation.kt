package com.example.todolistonline.mapper

import android.content.Context
import android.util.TypedValue
import javax.inject.Inject

class MapperPresentation @Inject constructor(
    private val context: Context
) {

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}