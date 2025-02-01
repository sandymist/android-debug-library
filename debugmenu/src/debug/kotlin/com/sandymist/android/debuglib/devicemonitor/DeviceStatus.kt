package com.sandymist.android.devicemonitor

import com.sandymist.android.debuglib.devicemonitor.audio.AudioStatus
import com.sandymist.android.debuglib.devicemonitor.audio.AudioStatusSerializer
import com.sandymist.android.debuglib.devicemonitor.network.INetworkStatus
import com.sandymist.android.debuglib.devicemonitor.network.INetworkStatusSerializer
import com.sandymist.android.debuglib.devicemonitor.power.PowerStatus
import com.sandymist.android.debuglib.devicemonitor.power.PowerStatusSerializer
import kotlinx.serialization.Serializable

@Serializable
data class DeviceStatus(
    @Serializable(with = INetworkStatusSerializer::class)
    val networkStatus: INetworkStatus,
    @Serializable(with = PowerStatusSerializer::class)
    val powerStatus: PowerStatus,
    @Serializable(with = AudioStatusSerializer::class)
    val audioStatus: AudioStatus,
)
