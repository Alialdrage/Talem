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
    val errorMessage: String? = null
)

class QuranAudioPlayer(private val context: Context) {

    private val TAG = "QuranAudioPlayer"
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

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
                    _playerState.value = _playerState.value.copy(
                        status = PlaybackStatus.PLAYING,
                        durationMs = mp.duration,
                        errorMessage = null
                    )
                    applySpeedAndVolume()
                    mp.start()
                    startProgressTracker()
                }
                setOnCompletionListener {
                    Log.d(TAG, "MediaPlayer completed")
                    stopProgressTracker()
                    if (_playerState.value.isRepeating) {
                        it.seekTo(0)
                        it.start()
                        _playerState.value = _playerState.value.copy(
                            status = PlaybackStatus.PLAYING,
                            currentPositionMs = 0
                        )
                        startProgressTracker()
                    } else {
                        _playerState.value = _playerState.value.copy(
                            status = PlaybackStatus.COMPLETED,
                            currentPositionMs = _playerState.value.durationMs
                        )
                    }
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    stopProgressTracker()
                    _playerState.value = _playerState.value.copy(
                        status = PlaybackStatus.ERROR,
                        errorMessage = "تعذر تشغيل الصوت. يرجى التحقق من اتصال الإنترنت."
                    )
                    true
                }
            }
        }
    }

    fun playUrl(url: String) {
        try {
            if (_playerState.value.currentUrl == url && mediaPlayer != null) {
                // Same audio
                when (_playerState.value.status) {
                    PlaybackStatus.PAUSED -> {
                        resume()
                        return
                    }
                    PlaybackStatus.STOPPED, PlaybackStatus.COMPLETED -> {
                        mediaPlayer?.seekTo(0)
                        mediaPlayer?.start()
                        _playerState.value = _playerState.value.copy(
                            status = PlaybackStatus.PLAYING,
                            currentPositionMs = 0
                        )
                        startProgressTracker()
                        return
                    }
                    PlaybackStatus.PLAYING -> {
                        return
                    }
                    else -> {}
                }
            }

            // New URL or player reset
            stop()
            initPlayer()
            _playerState.value = _playerState.value.copy(
                status = PlaybackStatus.BUFFERING,
                currentUrl = url,
                errorMessage = null,
                currentPositionMs = 0
            )

            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio URL: $url", e)
            _playerState.value = _playerState.value.copy(
                status = PlaybackStatus.ERROR,
                errorMessage = "خطأ في تحميل التلاوة: ${e.localizedMessage ?: "تأكد من الاتصال"}"
            )
        }
    }

    // 1. زر التشغيل والاستئناف
    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _playerState.value = _playerState.value.copy(status = PlaybackStatus.PLAYING)
                startProgressTracker()
            }
        }
    }

    // 2. زر الإيقاف المؤقت
    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                stopProgressTracker()
                _playerState.value = _playerState.value.copy(status = PlaybackStatus.PAUSED)
            }
        }
    }

    // 3. زر الإطفاء والإيقاف التام (Stop / Turn Off)
    fun stop() {
        stopProgressTracker()
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping media player", e)
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

    // التقديم والتأخير عبر شريط التقدم
    fun seekTo(positionMs: Int) {
        mediaPlayer?.let {
            val clamped = positionMs.coerceIn(0, _playerState.value.durationMs)
            it.seekTo(clamped)
            _playerState.value = _playerState.value.copy(currentPositionMs = clamped)
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
        mediaPlayer?.setVolume(clamped, clamped)
    }

    private fun applySpeedAndVolume() {
        mediaPlayer?.let { mp ->
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
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _playerState.value = _playerState.value.copy(
                            currentPositionMs = mp.currentPosition,
                            durationMs = if (mp.duration > 0) mp.duration else _playerState.value.durationMs
                        )
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
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing media player", e)
        }
        mediaPlayer = null
    }
}
