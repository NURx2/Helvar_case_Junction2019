package com.nevmem.helvarapp.activities

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.nevmem.helvarapp.R
import com.nevmem.helvarapp.permissions.ListenableActivity
import kotlinx.android.synthetic.main.lightning_mode_item.view.*
import kotlinx.android.synthetic.main.smart_mode_activity.*

class SmartModeActivity : ListenableActivity() {
    data class LightningMode(val name: String, val imageId: Int, var enabled: Boolean = false)
    private val modes = listOf(
        LightningMode("Work", R.drawable.book, false),
        LightningMode("Entertainment", R.drawable.tv, false),
        LightningMode("Dinner", R.drawable.food, false))

    private val allViews = ArrayList<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.smart_mode_activity)

        window.statusBarColor = resources.getColor(R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val inflater = LayoutInflater.from(this)
        val rows = ArrayList<LinearLayout>()
        for (i in 0..(modes.size + 1) / 2) {
            val row = inflater.inflate(R.layout.lightning_mode_row, modesAnchor, false) as LinearLayout
            rows.add(row)
        }

        modes.forEachIndexed { index, mode ->
            val view = inflater.inflate(R.layout.lightning_mode_item, rows[index / 2], false)
            val vh = ViewHolder(view)
            vh.image.setImageResource(mode.imageId)
            vh.caption.text = mode.name
            allViews.add(vh)
            rows[index / 2].addView(view)
        }

        rows.forEach { modesAnchor.addView(it) }

        allViews.forEach {
            it.setOnClickListener {
                revalidateImages(it)
            }
        }
        revalidateImages(null)

        backButton.setOnClickListener {
            finishAfterTransition()
        }
    }

    private fun revalidateImages(it: ViewHolder?) {
        allViews.forEach { innerIt ->
            if (innerIt == it) {
                innerIt.image.clearColorFilter()
                innerIt.image.alpha = 1f
            } else {
                val matrix = ColorMatrix()
                matrix.setSaturation(0f)
                innerIt.image.colorFilter = ColorMatrixColorFilter(matrix)
                innerIt.image.alpha = 0.5f
            }
        }
    }

    private class ViewHolder(private val view: View) {
        val image = view.lightningModeImage
        val caption = view.modeCaption

        fun setOnClickListener(cb: ()->Unit) {
            view.setOnClickListener { cb() }
        }
    }
}