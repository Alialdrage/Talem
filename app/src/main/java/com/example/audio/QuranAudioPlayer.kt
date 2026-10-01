package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class PlaybackStatus {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSED,
    STOPPED,
    COMPLETED,
    ERROR
}

data class PlayerState(
    val status: PlaybackStatus = PlaybackStatus.IDLE,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val isRepeating: Boolean = false,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val currentUrl: String = "",
    val isLiveStream: Boolean = false,
    val errorMessage: String? = null
)

class QuranAudioPlayer(private val context: Context) {

    private val TAG = "QuranAudioPlayer"
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    // Track whether the MediaPlayer is currently in a prepared state
    @Volatile
    private var isPrepared: Boolean = false

    // Position requested by user while player was buffering, idle, or stopped
    private var pendingSeekPositionMs: Int? = null

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private fun initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnPreparedListener { mp ->
                    Log.d(TAG, "MediaPlayer prepared, duration=${mp.duration}")
                    isPrepared = true
                    val isLive = _playerState.value.isLiveStream
                    val duration = if (!isLive && mp.duration > 0) mp.duration else 0

                    // If a seek was queued during buffering/idle and not live, apply it now safely
                    val targetSeek = if (!isLive) pendingSeekPositionMs else null
                    pendingSeekPositionMs = null
                    val startPos = if (targetSeek != null && duration > 0) {
                        val clamped = targetSeek.coerceIn(0, duration)
                        try {
                            mp.seekTo(clamped)
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to apply queued seek in onPrepared", e)
                        }
                        clamped
                    } else {
                        0
                    }

                    _playerState.value = _playerState.value.copy(
                        status = PlaybackStatus.PLAYING,
                        durationMs = duration,
                        currentPositionMs = startPos,
                        errorMessage = null
                    )
                    applySpeedAndVolume()
                    try {
                        mp.start()
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to start playback in onPrepared", e)
                    }
                    startProgressTracker()
                }
                setOnCompletionListener { mp ->
                    Log.d(TAG, "MediaPlayer completed")
                    stopProgressTracker()
                    if (_playerState.value.isRepeating) {
                        try {
                            if (isPrepared) {
                                mp.seekTo(0)
                                mp.start()
                                _playerState.value = _playerState.value.copy(
                                    status = PlaybackStatus.PLAYING,
                                    currentPositionMs = 0
                                )
                                startProgressTracker()
                                return@setOnCompletionListener
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to repeat playback", e)
                        }
                    }

                    _playerState.value = _playerState.value.copy(
                        status = PlaybackStatus.COMPLETED,
                        currentPositionMs = _playerState.value.durationMs
                    )
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    isPrepared = false
                    stopProgressTracker()
                    try {
                        mp.reset()
                    } catch (e: Exception) {
                        Log.w(TAG, "Error resetting MediaPlayer after error", e)
                    }
                    val userErrorMessage = if (what == -38) {
                        null
                    } else {
                        "تعذر تشغيل الصوت. يرجى التحقق من اتصال الإنترنت."
                    }
                    _playerState.value = _playerState.value.copy(
                        status = if (what == -38) PlaybackStatus.STOPPED else PlaybackStatus.ERROR,
                        errorMessage = userErrorMessage
                    )
                    true
                }
            }
        }
    }

    fun playUrl(url: String, isLive: Boolean = false) {
        if (url.isBlank()) return
        try {
            if (_playerState.value.currentUrl == url && mediaPlayer != null) {
                when (_playerState.value.status) {
                    PlaybackStatus.PAUSED -> {
                        resume()
                        return
                    }
                    PlaybackStatus.BUFFERING -> {
                        // Already preparing this URL
                        return
                    }
                    PlaybackStatus.PLAYING -> {
                        return
                    }
                    else -> {
                        // Was STOPPED, COMPLETED, or ERROR - re-prepare cleanly
                    }
                }
            }

            // Clean stop and prepare
            stopProgressTracker()
            isPrepared = false
            initPlayer()

            _playerState.value = _playerState.value.copy(
                status = PlaybackStatus.BUFFERING,
                currentUrl = url,
                isLiveStream = isLive,
                errorMessage = null,
                currentPositionMs = if (isLive) 0 else (pendingSeekPositionMs ?: 0)
            )

            mediaPlayer?.apply {
                reset()
                setDataSource(url)
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio URL: $url", e)
            isPrepared = false
            _playerState.value = _playerState.value.copy(
                status = PlaybackStatus.ERROR,
                errorMessage = "خطأ في تحميل التلاوة: ${e.localizedMessage ?: "تأكد من الاتصال"}"
            )
        }
    }

    // 1. زر التشغيل والاستئناف
    fun resume() {
        val mp = mediaPlayer
        if (mp != null && isPrepared) {
            try {
                if (!mp.isPlaying) {
                    mp.start()
                    _playerState.value = _playerState.value.copy(status = PlaybackStatus.PLAYING)
                    startProgressTracker()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error in resume()", e)
            }
        } else if (_playerState.value.currentUrl.isNotBlank()) {
            playUrl(_playerState.value.currentUrl)
        }
    }

    // 2. زر الإيقاف المؤقت
    fun pause() {
        val mp = mediaPlayer
        if (mp != null && isPrepared) {
            try {
                if (mp.isPlaying) {
                    mp.pause()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error in pause()", e)
            }
        }
        stopProgressTracker()
        _playerState.value = _playerState.value.copy(status = PlaybackStatus.PAUSED)
    }

    // 3. زر الإطفاء والإيقاف التام (Stop / Turn Off)
    fun stop() {
        stopProgressTracker()
        isPrepared = false
        pendingSeekPositionMs = null
        try {
            mediaPlayer?.let { mp ->
                try {
                    if (mp.isPlaying) {
                        mp.stop()
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error stopping media player", e)
                }
                mp.reset()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error resetting media player on stop", e)
        }
        _playerState.value = _playerState.value.copy(
            status = PlaybackStatus.STOPPED,
            currentPositionMs = 0
        )
    }

    // 4. زر التكرار للتعليم
    fun toggleRepeat() {
        val nextRepeat = !_playerState.value.isRepeating
        _playerState.value = _playerState.value.copy(isRepeating = nextRepeat)
    }

    // التقديم والتأخير الآمن عبر شريط التقدم (Seek) مع حماية كاملة من استدعاء seekTo في حالة خاطئة
    fun seekTo(positionMs: Int) {
        if (_playerState.value.isLiveStream) {
            // البث المباشر الحي مستمر ولا يقبل التقديم والتأخير
            return
        }
        val duration = _playerState.value.durationMs
        val clamped = if (duration > 0) positionMs.coerceIn(0, duration) else positionMs.coerceAtLeast(0)
        _playerState.value = _playerState.value.copy(currentPositionMs = clamped)

        val mp = mediaPlayer
        val currentStatus = _playerState.value.status
        if (mp != null && isPrepared && (currentStatus == PlaybackStatus.PLAYING || currentStatus == PlaybackStatus.PAUSED || currentStatus == PlaybackStatus.COMPLETED)) {
            try {
                mp.seekTo(clamped)
            } catch (e: Exception) {
                Log.w(TAG, "Safe seekTo failed", e)
            }
        } else {
            // Player is either BUFFERING (preparing), IDLE, or STOPPED.
            // Queue the target seek position to apply as soon as MediaPlayer is prepared.
            pendingSeekPositionMs = clamped
            Log.d(TAG, "seekTo safely queued for $clamped ms while player status=$currentStatus (isPrepared=$isPrepared)")
        }
    }

    // سرعة القراءة للتعليم (0.75x للمبتدئين، 1.0x عادي، 1.25x مراجعة)
    fun setSpeed(speed: Float) {
        _playerState.value = _playerState.value.copy(speed = speed)
        applySpeedAndVolume()
    }

    // التحكم بمستوى الصوت
    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _playerState.value = _playerState.value.copy(volume = clamped)
        mediaPlayer?.let { mp ->
            try {
                mp.setVolume(clamped, clamped)
            } catch (e: Exception) {
                Log.w(TAG, "Could not set volume", e)
            }
        }
    }

    private fun applySpeedAndVolume() {
        val mp = mediaPlayer
        if (mp != null && isPrepared) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val currentParams = mp.playbackParams
                    currentParams.speed = _playerState.value.speed
                    mp.playbackParams = currentParams
                }
                mp.setVolume(_playerState.value.volume, _playerState.value.volume)
            } catch (e: Exception) {
                Log.w(TAG, "Could not set playback params", e)
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val mp = mediaPlayer
                if (mp != null && isPrepared) {
                    try {
                        if (mp.isPlaying) {
                            _playerState.value = _playerState.value.copy(
                                currentPositionMs = mp.currentPosition,
                                durationMs = if (mp.duration > 0) mp.duration else _playerState.value.durationMs
                            )
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error checking current position in progress tracker", e)
                    }
                }
                delay(300)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracker()
        isPrepared = false
        pendingSeekPositionMs = null
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing media player", e)
        }
        mediaPlayer = null
    }
}
