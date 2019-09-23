package im.dacer.kata.util.helper

import android.app.Activity
import android.widget.ImageView
import com.afollestad.materialdialogs.MaterialDialog
import im.dacer.kata.R


object DialogHelper {
    fun showAndroidQAlert(context: Activity) {
        val titleArray = arrayOf(context.getString(R.string.android_q_method_text_selection_action),
                context.getString(R.string.android_q_method_share),
                context.getString(R.string.android_q_method_shortcut))

        val picArray = arrayOf(R.drawable.tutorial_text_selection_action,
                R.drawable.tutorial_share,
                R.drawable.tutorial_shortcut)

        MaterialDialog.Builder(context)
                .title(R.string.android_q_clipboard_alert_title)
                .content(R.string.android_q_clipboard_alert_content)
                .items(*titleArray)
                .itemsCallback { _, _, position, _ ->
                    showPictureDialog(context, titleArray[position], picArray[position])
                }
                .autoDismiss(false)
                .positiveText(android.R.string.ok)
                .onPositive { dialog, _ -> dialog.hide() }
                .show()
    }

    private fun showPictureDialog(context: Activity, title: String, picResId: Int) {
        val picDialog = context.layoutInflater.inflate(R.layout.dialog_picture, null)
        picDialog.findViewById<ImageView>(R.id.imageView).setImageResource(picResId)
        MaterialDialog.Builder(context)
                .title(title)
                .customView(picDialog, true)
                .positiveText(android.R.string.ok)
                .show()
    }
}