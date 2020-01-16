package com.smartit.beunique.networking;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

public class VolleyCacheRequest extends Request<NetworkResponse> {

	private final Response.Listener<NetworkResponse> networkResponseListener;
	private final Response.ErrorListener errorListener;

	public VolleyCacheRequest(int method, String url, Response.Listener<NetworkResponse> responseListener, Response.ErrorListener errorListener) {
		super(method, url, errorListener);
		this.networkResponseListener = responseListener;
		this.errorListener = errorListener;
	}

	@Override
	protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
		Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
		if (cacheEntry == null) {
			cacheEntry = new Cache.Entry();
		}
		final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
		final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
		long now = System.currentTimeMillis();
		final long softExpire = now + cacheHitButRefreshed;
		final long ttl = now + cacheExpired;
		cacheEntry.data = response.data;
		cacheEntry.softTtl = softExpire;
		cacheEntry.ttl = ttl;
		String headerValue;
		headerValue = response.headers.get("Date");
		if (headerValue != null) {
			cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
		}
		headerValue = response.headers.get("Last-Modified");
		if (headerValue != null) {
			cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
		}
		cacheEntry.responseHeaders = response.headers;
		return Response.success(response, cacheEntry);
	}

	@Override
	protected VolleyError parseNetworkError(VolleyError volleyError) {
		return super.parseNetworkError(volleyError);
	}

	@Override
	protected void deliverResponse(NetworkResponse response) {
		this.networkResponseListener.onResponse(response);
	}

	@Override
	public void deliverError(VolleyError error) {
		this.errorListener.onErrorResponse(error);
	}

}
