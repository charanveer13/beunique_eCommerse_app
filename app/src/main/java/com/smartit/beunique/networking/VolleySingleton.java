package com.smartit.beunique.networking;

import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.util.ObjectUtil;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

	private static final String TAG = VolleySingleton.class.getSimpleName();
	private static VolleySingleton instance;
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;

	public static synchronized VolleySingleton getInstance() {
		if (instance == null) {
			instance = new VolleySingleton();
		}
		return instance;
	}

	//TODO this method is used to loaad image form network url on volly custom imageview : NetworkImageView class
	public ImageLoader getImageLoader() {
		if (this.imageLoader == null) {
			this.imageLoader = new ImageLoader(this.getRequestQueue(), new LruBitmapCache());
		}
		return this.imageLoader;
	}

	private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            this.requestQueue = Volley.newRequestQueue( ApplicationHelper.application().getContext());
        }
        return this.requestQueue;
    }

	//TODO add request in volley request queue
	public <T> void addToRequestQueue(Request<T> request, String requestTagName) {
		request.setTag( ObjectUtil.isEmpty(requestTagName) ? TAG : requestTagName);
		this.getRequestQueue().add(request);
	}

	public <T> void addToRequestQueue(Request<T> request) {
		request.setTag(TAG);
		this.getRequestQueue().add(request);
	}

	public void cancelPendingRequests(Object tag) {
		if (!ObjectUtil.isEmpty(this.requestQueue)) {
			this.requestQueue.cancelAll(tag);
		}
	}

}
