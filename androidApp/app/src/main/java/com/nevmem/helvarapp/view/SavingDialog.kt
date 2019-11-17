package com.nevmem.helvarapp.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialogFragment
import com.nevmem.helvarapp.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.saving_dialog.view.*

class SavingDialog(private val observable: Observable<Int>) : AppCompatDialogFragment() {
    private lateinit var marksDisposable: Disposable

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.DialogCustomTheme)
        val view = LayoutInflater.from(activity).inflate(R.layout.saving_dialog, null, false)

        marksDisposable = observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.marksCount.text = it.toString() }

        return builder.setView(view)
            .create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        marksDisposable.dispose()
    }
}