package org.fishinix.ueventpatch;
import de.robv.android.xposed.IXposedHookLoadPackage;
import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
public class MainHook implements IXposedHookLoadPackage {
    public static final String TAG = "uEventPatch";
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if ("android".equals(lpparam.packageName)) {
            XposedBridge.log("D/" + TAG + " Loaded");
            hook(lpparam);
        }
    }

    private void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.android.server.ExtconStateObserver",
                lpparam.classLoader,
                "onUEvent",
                "com.android.server.ExtconUEventObserver$ExtconInfo",
                "android.os.UEventObserver$UEvent",// 如果参数是宿主的类，你可以使用findClass来加载那个类或是填写那个类的完整名称！
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String name = (String) XposedHelpers.callMethod(param.args[1], "get", "NAME");

                        if(name == null) {
                            XposedBridge.log("D/" + TAG + " Removed NAME");
                            param.setResult(null);
                        }
                    }
                });
        XposedHelpers.findAndHookMethod("com.android.server.WiredAccessoryManager$WiredAccessoryObserver",
                lpparam.classLoader,
                "onUEvent",
                "android.os.UEventObserver$UEvent",// 如果参数是宿主的类，你可以使用findClass来加载那个类或是填写那个类的完整名称！
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String name =  (String) XposedHelpers.callMethod(param.args[0], "get", "NAME");
                        if(name == null) {
                            String switch_path = (String) XposedHelpers.callMethod(param.args[0], "get", "SWITCH_PATH");
                            if(switch_path == null) {
                                XposedBridge.log("D/" + TAG + " Removed SWITCH_PATH");
                                param.setResult(null);
                            }
                        }
                    }
                });

    }
}

