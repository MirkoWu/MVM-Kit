package com.mirkowu.lib_bluetooth.library.search.response;


import com.mirkowu.lib_bluetooth.library.search.SearchResult;

public interface BluetoothSearchResponse {
    void onSearchStarted();

    void onDeviceFounded(SearchResult device);

    void onSearchStopped();

    void onSearchCanceled();
}
