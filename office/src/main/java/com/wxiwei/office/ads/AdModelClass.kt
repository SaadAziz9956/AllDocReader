package com.wxiwei.office.ads

import androidx.annotation.Keep

@Keep
data class AdModelClass(
    var title: String,
    var body: String,
    var actionBtnTxt: String,
    var imageUri: String,
    var iconImgUri: String,
    var adType: String,
)