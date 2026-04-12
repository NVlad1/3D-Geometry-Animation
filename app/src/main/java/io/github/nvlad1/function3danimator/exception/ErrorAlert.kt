package io.github.nvlad1.function3danimator.exception

import android.app.AlertDialog
import android.content.Context

/**
 * Created by Vlad on 25.12.2015.
 */
class ErrorAlert {
    var errCode = 0
    var exprID = -1

    val isError: Boolean
        get() = errCode != 0

    fun show(c0: Context?) {
        val builder = AlertDialog.Builder(c0)
        if (errCode == 1) {
            if (exprID == -1) {
                builder.setTitle("error")
                        .setMessage("Cannot parse expression")
                        .setCancelable(false)
                        .setNegativeButton("ok"
                        ) { dialog, id -> dialog.cancel() }
            } else {
                builder.setTitle("error")
                        .setMessage("Cannot parse function " + (exprID + 1).toString())
                        .setCancelable(false)
                        .setNegativeButton("ok"
                        ) { dialog, id -> dialog.cancel() }
            }
        }
        if (errCode == 2) {
            builder.setTitle("error")
                    .setMessage("error code=2")
                    .setCancelable(false)
                    .setNegativeButton("ok"
                    ) { dialog, id -> dialog.cancel() }
        }
        if (errCode == 3) {
            builder.setTitle("error")
                    .setMessage("error in graph analysis")
                    .setCancelable(false)
                    .setNegativeButton("ok"
                    ) { dialog, id -> dialog.cancel() }
        }
        if (errCode == 4) {
            builder.setTitle("error")
                    .setMessage("Wrong number format")
                    .setCancelable(false)
                    .setNegativeButton("ok"
                    ) { dialog, id -> dialog.cancel() }
        }
        if (errCode == 5) {
            builder.setTitle("error")
                    .setMessage("Grid surface is too big")
                    .setCancelable(false)
                    .setNegativeButton("ok"
                    ) { dialog, id -> dialog.cancel() }
        }
        val alert = builder.create()
        alert.show()
    }

    fun show(c0: Context?, code: Int) {
        errCode = code
        exprID = -1
        show(c0)
    }

    fun show(c0: Context?, code: Int, exprID: Int) {
        if (code != 1) return
        errCode = code
        this.exprID = exprID
        show(c0)
    }
}