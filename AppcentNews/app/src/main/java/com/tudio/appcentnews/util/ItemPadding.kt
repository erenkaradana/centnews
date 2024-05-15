package com.tudio.appcentnews.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// RecyclerView'da haber nesneleri (kartları) arasında padding bırakabilmek için gerekli utility sınıfı.

class ItemPadding(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = space
        outRect.bottom = space
    }
}
