package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class TrafficSign(
    val title: String,
    val description: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val symbolType: String
)

enum class GearState { PARK, REVERSE, NEUTRAL, DRIVE }
enum class TrafficLightState { RED, YELLOW, GREEN }
enum class WeatherMode { CLEAR, RAIN, FOG, NIGHT }

data class SimulatorUiState(
    val speedKmH: Float = 0f,
    val steeringAngleDeg: Float = 0f,
    val gear: GearState = GearState.PARK,
    val isEngineOn: Boolean = false,
    val isSeatbeltFastened: Boolean = false,
    val isHandbrakeEngaged: Boolean = true,
    val isClutchPressed: Boolean = false,
    val isHazardLightOn: Boolean = false,
    val isWiperOn: Boolean = false,
    val isHighBeamOn: Boolean = false,
    val isHornActive: Boolean = false,
    val isLeftSignalOn: Boolean = false,
    val isRightSignalOn: Boolean = false,
    val isHeadlightsOn: Boolean = true,
    val weather: WeatherMode = WeatherMode.CLEAR,
    val trafficLight: TrafficLightState = TrafficLightState.GREEN,
    val distanceCoveredMeters: Float = 0f,
    val safetyScore: Int = 100,
    val ruleViolationAlert: String? = null,
    val isStudentAccessBypassed: Boolean = false,
    val currentSignIndex: Int = 0,
    val quizScore: Int = 0,
    val quizAnswered: Boolean = false,
    val quizSelectedOption: Int? = null,
    val parkingAccuracyPercent: Int = 92,
    // SENSOR & PARKING CHALLENGE EXTENSIONS
    val isTiltSteeringEnabled: Boolean = false,
    val isParkingChallengeActive: Boolean = false,
    val parkingProximityDistanceMeters: Float = 1.8f,
    val isParkingSuccess: Boolean = false,
    // GAME MODE EXTENSIONS
    val isNitroActive: Boolean = false,
    val nitroChargePercent: Float = 100f,
    val gameXpScore: Int = 1250,
    val comboMultiplier: Int = 1,
    val currentMissionText: String = "🏆 MISSION: Drive 100m smoothly on Indian Left Lane",
    val floatXpPopup: String? = null
)

class SimulatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SimulatorUiState())
    val uiState: StateFlow<SimulatorUiState> = _uiState.asStateFlow()

    val trafficSignsList = listOf(
        TrafficSign(
            title = "Stop & Give Way",
            description = "Octagonal red sign with white border.",
            options = listOf("Proceed without stopping", "Mandatory Complete Stop", "Speed Up", "No Parking"),
            correctAnswerIndex = 1,
            symbolType = "STOP"
        ),
        TrafficSign(
            title = "No U-Turn",
            description = "Circular red border with crossed U-curve.",
            options = listOf("U-Turn Allowed", "Compulsory U-Turn", "Prohibited U-Turn", "One Way Street"),
            correctAnswerIndex = 2,
            symbolType = "NO_UTURN"
        ),
        TrafficSign(
            title = "Pedestrian Crossing",
            description = "Blue triangle with walking figure.",
            options = listOf("Pedestrian Crossing Ahead", "School Zone Only", "No Walking", "Hospital Ahead"),
            correctAnswerIndex = 0,
            symbolType = "PEDESTRIAN"
        ),
        TrafficSign(
            title = "Steep Hill Ascent",
            description = "Triangle with car climbing incline.",
            options = listOf("Low Gear Required for Climb", "Downhill Ahead", "Speed Limit 80", "Slippery Road"),
            correctAnswerIndex = 0,
            symbolType = "STEEP_HILL"
        )
    )

    init {
        // Run simulator tick loop for continuous road movement and traffic signals
        viewModelScope.launch {
            var lightTimer = 0
            while (true) {
                delay(50)
                val state = _uiState.value
                if (state.isEngineOn && state.speedKmH > 0) {
                    val deltaDist = (state.speedKmH / 3.6f) * 0.05f
                    val newDist = state.distanceCoveredMeters + deltaDist

                    var newProximity = state.parkingProximityDistanceMeters
                    var isSuccess = state.isParkingSuccess

                    if (state.isParkingChallengeActive && state.gear == GearState.REVERSE) {
                        newProximity = (state.parkingProximityDistanceMeters - deltaDist * 0.4f).coerceIn(0.1f, 3.0f)
                        if (newProximity in 0.3f..0.6f && !isSuccess) {
                            isSuccess = true
                        }
                    }

                    _uiState.value = state.copy(
                        distanceCoveredMeters = newDist,
                        parkingProximityDistanceMeters = newProximity,
                        isParkingSuccess = isSuccess
                    )
                }

                lightTimer++
                if (lightTimer % 120 == 0) {
                    val nextLight = when (state.trafficLight) {
                        TrafficLightState.GREEN -> TrafficLightState.YELLOW
                        TrafficLightState.YELLOW -> TrafficLightState.RED
                        TrafficLightState.RED -> TrafficLightState.GREEN
                    }
                    _uiState.value = _uiState.value.copy(trafficLight = nextLight)
                }
            }
        }
    }

    fun toggleTiltSteering() {
        val next = !_uiState.value.isTiltSteeringEnabled
        _uiState.value = _uiState.value.copy(
            isTiltSteeringEnabled = next,
            ruleViolationAlert = if (next) "📱 Gyroscope / Tilt Steering: ACTIVATED" else "Touch Steering: ACTIVATED"
        )
    }

    fun toggleParkingChallenge() {
        val next = !_uiState.value.isParkingChallengeActive
        _uiState.value = _uiState.value.copy(
            isParkingChallengeActive = next,
            gear = if (next) GearState.REVERSE else _uiState.value.gear,
            parkingProximityDistanceMeters = 2.4f,
            isParkingSuccess = false,
            ruleViolationAlert = if (next) "🅿️ PARKING CHALLENGE: Reverse into bay (0.4m target zone)" else "Free Drive Mode"
        )
    }

    fun toggleEngine() {
        val state = _uiState.value
        if (!state.isSeatbeltFastened && !state.isEngineOn) {
            _uiState.value = state.copy(
                ruleViolationAlert = "⚠️ Indian Safety Rule: Fasten seatbelt before starting engine!"
            )
            return
        }
        val next = !state.isEngineOn
        _uiState.value = state.copy(
            isEngineOn = next,
            speedKmH = if (!next) 0f else state.speedKmH,
            gear = if (!next) GearState.PARK else state.gear,
            ruleViolationAlert = if (next) "Engine Started • Indian RHD System Ready" else "Engine Stopped"
        )
    }

    fun toggleSeatbelt() {
        val next = !_uiState.value.isSeatbeltFastened
        _uiState.value = _uiState.value.copy(
            isSeatbeltFastened = next,
            ruleViolationAlert = if (next) "Seatbelt Fastened (RTO Rule Compliant)" else "⚠️ Warning: Unfastened Seatbelt!"
        )
    }

    fun toggleHandbrake() {
        val next = !_uiState.value.isHandbrakeEngaged
        _uiState.value = _uiState.value.copy(
            isHandbrakeEngaged = next,
            speedKmH = if (next) 0f else _uiState.value.speedKmH,
            ruleViolationAlert = if (next) "Handbrake Engaged (P)" else "Handbrake Disengaged"
        )
    }

    fun setClutchPressed(pressed: Boolean) {
        _uiState.value = _uiState.value.copy(isClutchPressed = pressed)
    }

    fun toggleHazard() {
        val next = !_uiState.value.isHazardLightOn
        _uiState.value = _uiState.value.copy(
            isHazardLightOn = next,
            isLeftSignalOn = false,
            isRightSignalOn = false
        )
    }

    fun toggleWiper() {
        _uiState.value = _uiState.value.copy(isWiperOn = !_uiState.value.isWiperOn)
    }

    fun pressHorn() {
        _uiState.value = _uiState.value.copy(
            isHornActive = true,
            ruleViolationAlert = "🔊 Horn Sounded (Use sparingly in Silence Zones)"
        )
        viewModelScope.launch {
            delay(1000)
            _uiState.value = _uiState.value.copy(isHornActive = false)
        }
    }

    fun bypassStudentAccess() {
        _uiState.value = _uiState.value.copy(isStudentAccessBypassed = true)
    }

    fun clearAlert() {
        _uiState.value = _uiState.value.copy(ruleViolationAlert = null)
    }

    fun setGear(gear: GearState) {
        if (!_uiState.value.isEngineOn && gear != GearState.PARK) {
            _uiState.value = _uiState.value.copy(ruleViolationAlert = "⚠️ Turn engine ON first!")
            return
        }
        _uiState.value = _uiState.value.copy(gear = gear, ruleViolationAlert = "Gear changed to ${gear.name}")
    }

    fun accelerate() {
        val state = _uiState.value
        if (!state.isEngineOn) {
            _uiState.value = state.copy(ruleViolationAlert = "⚠️ Engine is OFF. Start ignition!")
            return
        }
        if (state.isHandbrakeEngaged) {
            _uiState.value = state.copy(ruleViolationAlert = "⚠️ Handbrake is ENGAGED! Disengage handbrake first.")
            return
        }
        if (state.gear == GearState.PARK || state.gear == GearState.NEUTRAL) {
            _uiState.value = state.copy(ruleViolationAlert = "⚠️ Shift gear to DRIVE (D) or REVERSE (R) to move!")
            return
        }
        val maxSpeed = if (state.gear == GearState.REVERSE) 25f else 100f
        val newSpeed = (state.speedKmH + 4f).coerceAtMost(maxSpeed)

        var violation: String? = null
        if (newSpeed > 40f && state.gear == GearState.DRIVE) {
            violation = "⚠️ Speed Limit Alert: City Limit 40 km/h Exceeded!"
        }

        _uiState.value = state.copy(speedKmH = newSpeed, ruleViolationAlert = violation)
    }

    fun applyBrake() {
        val state = _uiState.value
        val newSpeed = (state.speedKmH - 8f).coerceAtLeast(0f)
        _uiState.value = state.copy(speedKmH = newSpeed)
    }

    fun updateSteering(angle: Float) {
        _uiState.value = _uiState.value.copy(steeringAngleDeg = angle.coerceIn(-180f, 180f))
    }

    fun toggleLeftSignal() {
        _uiState.value = _uiState.value.copy(
            isLeftSignalOn = !_uiState.value.isLeftSignalOn,
            isRightSignalOn = false
        )
    }

    fun toggleRightSignal() {
        _uiState.value = _uiState.value.copy(
            isRightSignalOn = !_uiState.value.isRightSignalOn,
            isLeftSignalOn = false
        )
    }

    fun toggleHeadlights() {
        _uiState.value = _uiState.value.copy(isHeadlightsOn = !_uiState.value.isHeadlightsOn)
    }

    fun setWeather(mode: WeatherMode) {
        _uiState.value = _uiState.value.copy(
            weather = mode,
            isHeadlightsOn = (mode == WeatherMode.NIGHT || mode == WeatherMode.FOG || _uiState.value.isHeadlightsOn)
        )
    }

    fun triggerNitro() {
        val state = _uiState.value
        if (!state.isEngineOn) {
            _uiState.value = state.copy(ruleViolationAlert = "⚠️ Start engine to ignite Nitro Boost!")
            return
        }
        if (state.nitroChargePercent < 20f) {
            _uiState.value = state.copy(ruleViolationAlert = "⚠️ Nitro Depleted! Recharge by driving smoothly.")
            return
        }
        val newSpeed = (state.speedKmH + 25f).coerceAtMost(130f)
        val newCharge = (state.nitroChargePercent - 30f).coerceAtLeast(0f)
        val newXp = state.gameXpScore + 150
        _uiState.value = state.copy(
            isNitroActive = true,
            speedKmH = newSpeed,
            nitroChargePercent = newCharge,
            gameXpScore = newXp,
            comboMultiplier = state.comboMultiplier + 1,
            ruleViolationAlert = "🚀 NITRO BOOST IGNITED! +150 XP",
            floatXpPopup = "+150 XP NITRO!"
        )

        viewModelScope.launch {
            delay(1500)
            _uiState.value = _uiState.value.copy(isNitroActive = false, floatXpPopup = null)
        }
    }

    fun addGameXp(points: Int, reason: String) {
        val state = _uiState.value
        _uiState.value = state.copy(
            gameXpScore = state.gameXpScore + points,
            floatXpPopup = "+$points XP $reason"
        )
        viewModelScope.launch {
            delay(2000)
            _uiState.value = _uiState.value.copy(floatXpPopup = null)
        }
    }

    fun answerQuizOption(optionIndex: Int) {
        val state = _uiState.value
        if (state.quizAnswered) return

        val currentSign = trafficSignsList[state.currentSignIndex]
        val isCorrect = (optionIndex == currentSign.correctAnswerIndex)
        val newScore = if (isCorrect) state.quizScore + 25 else state.quizScore

        _uiState.value = state.copy(
            quizAnswered = true,
            quizSelectedOption = optionIndex,
            quizScore = newScore
        )
    }

    fun nextQuizSign() {
        val state = _uiState.value
        val nextIndex = (state.currentSignIndex + 1) % trafficSignsList.size
        _uiState.value = state.copy(
            currentSignIndex = nextIndex,
            quizAnswered = false,
            quizSelectedOption = null
        )
    }
}
