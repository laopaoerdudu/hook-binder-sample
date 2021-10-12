package com.dev

import android.os.IBinder
import java.lang.reflect.Proxy

class HookBinderHelper {
    companion object {

        // public static IBinder getService(String name) {
        //    try {
        //        IBinder service = sCache.get(name);
        //        if (service != null) {
        //            return service;
        //        } else {
        //            return getIServiceManager().getService(name);
        //        }
        //    } catch (RemoteException e) {
        //        Log.e(TAG, "error in getService", e);
        //    }
        //    return null;
        //}
        fun hookClipboardService() {
            try {
                val serviceManagerClass = Class.forName("android.os.ServiceManager")

                // ServiceManager里面管理的 Clipboard Binder对象
                val rawBinder = serviceManagerClass
                    .getDeclaredMethod("getService", String::class.java).apply {
                        isAccessible = true
                    }.invoke(null, "clipboard") as? IBinder

                val stubBinder = Proxy.newProxyInstance(
                    serviceManagerClass.classLoader,
                    arrayOf(IBinder::class.java),
                    StubIBinderProxy(rawBinder)
                ) as? IBinder

                val cache = (serviceManagerClass.getDeclaredField("sCache").apply {
                    isAccessible = true
                }.get(null) as? MutableMap<String?, IBinder?>)
                cache?.put("clipboard", stubBinder)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}