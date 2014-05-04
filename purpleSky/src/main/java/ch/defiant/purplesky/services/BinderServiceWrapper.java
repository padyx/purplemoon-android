package ch.defiant.purplesky.services;

import android.app.Service;
import android.os.Binder;

public class BinderServiceWrapper<T extends Service> extends Binder {

    private final T m_service;

    public BinderServiceWrapper(T service) {
        m_service = service;
    }

    public T getService() {
        return m_service;
    }
}
