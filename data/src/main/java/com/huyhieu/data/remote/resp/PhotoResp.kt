package com.huyhieu.data.remote.resp


import com.google.gson.annotations.SerializedName

class PhotoResp(
    @SerializedName("alt")
    val alt: String = "",
    @SerializedName("avg_color")
    val avgColor: String = "",
    @SerializedName("height")
    val height: Int = 0,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("liked")
    val liked: Boolean = false,
    @SerializedName("photographer")
    val photographer: String = "",
    @SerializedName("photographer_id")
    val photographerId: String = "",
    @SerializedName("photographer_url")
    val photographerUrl: String = "",
    @SerializedName("src")
    val src: SrcResp = SrcResp(),
    @SerializedName("url")
    val url: String = "",
    @SerializedName("width")
    val width: Int = 0,
)