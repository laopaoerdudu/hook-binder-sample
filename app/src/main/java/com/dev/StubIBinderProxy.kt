package com.dev

import android.os.IBinder
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class StubIBinderProxy(private val binder: IBinder?) : InvocationHandler {
    private var stub: Class<out Any>? = null
    private var mInterface: Class<out Any>? = null // android.content.IClipboard

    init {
        try {
            stub = Class.forName("android.content.IClipboard\$Stub")
            this.mInterface = Class.forName("android.content.IClipboard")
        } catch (ex: ClassNotFoundException) {
            ex.printStackTrace()
        }
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        if ("queryLocalInterface" == method?.name) {
            // 返回了我们伪造的系统服务对象
            return Proxy.newProxyInstance(
                proxy?.javaClass?.classLoader,
                arrayOf(this.mInterface),
                stub?.let { SystemServiceProxy(binder, it) }
            )
        }
        return method?.invoke(mInterface, args)
    }

}