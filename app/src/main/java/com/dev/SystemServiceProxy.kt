package com.dev

import android.content.ClipData
import android.os.IBinder
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

// 伪造一个系统服务对象
// IBinder b = ServiceManager.getService("service_name"); // 获取原始的 IBinder 对象
// IXXInterface in = IXXInterface.Stub.asInterface(b); // 转换为 Service 接口
class SystemServiceProxy(binder: IBinder?, stubClass: Class<out Any>) :
    InvocationHandler {
    private var service: Any? = null

    init {
        try {
            //  public static com.dev.XXX asInterface(android.os.IBinder obj)
            this.service = stubClass.getDeclaredMethod("asInterface", IBinder::class.java).apply {
                isAccessible = true
            }.invoke(null, binder)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        // 替换剪切板的内容
        if ("getPrimaryClip" == method?.name) {
            return ClipData.newPlainText(null, "WWE Super Start")
        }

        // 欺骗系统,剪切板上一直有内容
        if ("hasPrimaryClip" == method?.name) {
            return true
        }
        return method?.invoke(service, args)
    }
}