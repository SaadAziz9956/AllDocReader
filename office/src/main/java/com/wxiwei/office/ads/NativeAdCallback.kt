package com.wxiwei.office.ads

interface NativeAdCallback {
    fun onNewAdLoaded(nativeAd: Any, position: Int)
    fun onAdClicked(position: Int)
}