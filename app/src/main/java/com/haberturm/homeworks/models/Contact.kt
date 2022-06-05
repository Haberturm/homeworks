package com.haberturm.homeworks.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val name: String,
    val surname: String,
    val phoneNumber: String
    ) : Parcelable

