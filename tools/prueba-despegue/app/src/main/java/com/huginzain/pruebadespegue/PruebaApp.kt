package com.huginzain.pruebadespegue

import android.app.Application
import android.content.Context
import android.util.Log
import dji.v5.common.error.IDJIError
import dji.v5.common.register.DJISDKInitEvent
import dji.v5.manager.SDKManager
import dji.v5.manager.interfaces.SDKManagerCallback

/** Arranca el MSDK y registra la App Key al iniciar la app. */
class PruebaApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        com.cySdkyc.clx.Helper.install(this) // obligatorio desde MSDK 5.10.0
    }

    override fun onCreate() {
        super.onCreate()
        SDKManager.getInstance().init(this, object : SDKManagerCallback {
            override fun onInitProcess(event: DJISDKInitEvent, totalProcess: Int) {
                if (event == DJISDKInitEvent.INITIALIZE_COMPLETE) SDKManager.getInstance().registerApp()
            }

            override fun onRegisterSuccess() = report("App Key registrada")
            override fun onRegisterFailure(error: IDJIError) = report("Fallo al registrar la App Key: ${error.description() ?: error.errorCode()}")
            override fun onProductConnect(productId: Int) = report("Producto DJI conectado (id $productId)")
            override fun onProductDisconnect(productId: Int) = report("Producto DJI desconectado (id $productId)")
            override fun onProductChanged(productId: Int) = Unit
            override fun onDatabaseDownloadProgress(current: Long, total: Long) = Unit
        })
    }

    private fun report(msg: String) {
        Log.i(TAG, msg)
        sdkStatus = msg
    }

    companion object {
        const val TAG = "PruebaDespegue"

        /** Último estado del MSDK, para mostrarlo en pantalla. */
        @Volatile
        var sdkStatus = "Iniciando MSDK…"
    }
}
