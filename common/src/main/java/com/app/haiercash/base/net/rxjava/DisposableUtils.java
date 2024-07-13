package com.app.haiercash.base.net.rxjava;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Author: Sun<br/>
 * Date :    2018/11/6<br/>
 * FileName: DisposableUtils<br/>
 * Description: 用于辅助rxjava的解绑<br/>
 */
public class DisposableUtils {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    /**
     * 添加网络请求
     */
    public void addDisposable(Disposable disposable) {
        if (compositeDisposable != null) {
            compositeDisposable.add(disposable);
        }
    }

    /**
     * 断开网络请求
     */
    public void removeDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }
}
