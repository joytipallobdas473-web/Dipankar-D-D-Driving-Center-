package com.example.ui.components

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.util.Locale

enum class RtoQuizLanguage(val label: String, val code: String) {
    ENGLISH("English 🇬🇧", "en"),
    HINDI("हिन्दी 🇮🇳", "hi"),
    BENGALI("বাংলা 🇧🇩", "bn")
}

data class RtoQuestion(
    val id: Int,
    val questionText: String,
    val options: List<String>,
    val explanation: String,
    val hiQuestionText: String,
    val hiOptions: List<String>,
    val hiExplanation: String,
    val bnQuestionText: String,
    val bnOptions: List<String>,
    val bnExplanation: String,
    val correctAnswerIndex: Int,
    val category: String,
    val icon: String
)

@Composable
fun RtoQuizComponent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(RtoQuizLanguage.ENGLISH) }
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TTS ready
            }
        }
        textToSpeech = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    fun speakText(text: String, lang: RtoQuizLanguage) {
        textToSpeech?.let { tts ->
            val locale = when (lang) {
                RtoQuizLanguage.ENGLISH -> Locale.US
                RtoQuizLanguage.HINDI -> Locale("hi", "IN")
                RtoQuizLanguage.BENGALI -> Locale("bn", "BD")
            }
            tts.language = locale
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "RTO_TTS_ID")
        }
    }

    val questions = remember {
        listOf(
            RtoQuestion(
                id = 1,
                questionText = "What does a circular traffic sign with a red border and a white background signify?",
                options = listOf(
                    "Mandatory / Regulatory Sign",
                    "Warning / Cautionary Sign",
                    "Informational Sign",
                    "Temporary Construction Sign"
                ),
                explanation = "Red circular signs indicate mandatory orders (e.g. Stop, Speed Limit, No Parking) that must be strictly obeyed by law.",
                hiQuestionText = "लाल बॉर्डर वाले गोल यातायात संकेत का क्या अर्थ है?",
                hiOptions = listOf("अनिवार्य / नियामक आदेश", "चेतावनी संकेत", "सूचनात्मक संकेत", "निर्माण क्षेत्र"),
                hiExplanation = "लाल गोल संकेत अनिवार्य नियमों को दर्शाते हैं जिनका पालन करना कानूनन जरूरी है।",
                bnQuestionText = "লাল সীমানা বিশিষ্ট বৃত্তাকার ট্রাফিক চিহ্নের অর্থ কী?",
                bnOptions = listOf("বাধ্যতামূলক / নিয়ন্ত্রক আদেশ", "সতর্কতামূলক সংকেত", "তথ্যমূলক চিহ্ন", "নির্মাণ এলাকা"),
                bnExplanation = "লাল বৃত্তাকার চিহ্ন বাধ্যতামূলক আইন নির্দেশ করে।",
                correctAnswerIndex = 0,
                category = "Traffic Signs",
                icon = "🛑"
            ),
            RtoQuestion(
                id = 2,
                questionText = "When approaching an uncontrolled road intersection, who has the right of way?",
                options = listOf(
                    "The vehicle approaching from the left",
                    "The vehicle approaching from the right",
                    "The larger vehicle",
                    "The vehicle driving faster"
                ),
                explanation = "According to Indian RTO Traffic Rules, priority is given to vehicles approaching from your right at uncontrolled intersections.",
                hiQuestionText = "अनियंत्रित चौराहे पर किस वाहन को जाने की प्राथमिकता होती है?",
                hiOptions = listOf("बाईं ओर का वाहन", "दाईं ओर से आने वाले वाहन", "बड़ा वाहन", "तेज गति वाला वाहन"),
                hiExplanation = "भारतीय आरटीओ नियमों के अनुसार हमेशा दाईं तरफ के वाहन को पहली प्राथमिकता मिलती है।",
                bnQuestionText = "অনিয়ন্ত্রিত মোড়ে কার যাতায়াতের অগ্রাধিকার রয়েছে?",
                bnOptions = listOf("বাম দিকের যান", "ডান দিক থেকে আসা যানবাহন", "বৃহত্তর যান", "দ্রুতগামী যান"),
                bnExplanation = "ভারতীয় আরটিও নিয়ম অনুযায়ী ডান দিক থেকে আগত যানবাহনকে অগ্রাধিকার দিতে হয়।",
                correctAnswerIndex = 1,
                category = "Right of Way",
                icon = "🏎️"
            ),
            RtoQuestion(
                id = 3,
                questionText = "What is the mandatory minimum distance you must maintain while following another vehicle in city traffic?",
                options = listOf(
                    "At least 2-Second Gap rule or 1 car length per 10 km/h speed",
                    "50 centimeters",
                    "Only 1 meter",
                    "No minimum distance required"
                ),
                explanation = "The 2-Second gap rule gives adequate reaction time to brake safely if the lead vehicle stops suddenly.",
                hiQuestionText = "शहर के यातायात में वाहनों के बीच सुरक्षित दूरी कितनी होनी चाहिए?",
                hiOptions = listOf("2-सेकंड का नियम (1 कार लंबाई / 10 किमी/घंटा)", "50 सेंटीमीटर", "केवल 1 मीटर", "कोई दूरी जरूरी नहीं"),
                hiExplanation = "2-सेकंड की दूरी अचानक ब्रेक लगाने की स्थिति में सुरक्षित समय देती है।",
                bnQuestionText = "শহরের যানজটে সামনের গাড়ির থেকে নিরাপদ দূরত্ব কত রাখা উচিত?",
                bnOptions = listOf("২-সেকেন্ডের নিয়ম (প্রতি ১০ কিমি গতিতে ১ গাড়ির দৈর্ঘ্য)", "৫০ সেন্টিমিটার", "১ মিটার", "কোন দূরত্বের প্রয়োজন নেই"),
                bnExplanation = "২-সেকেন্ডের নিয়ম সংঘর্ষ এড়াতে সাহায্য করে।",
                correctAnswerIndex = 0,
                category = "Road Safety",
                icon = "📏"
            ),
            RtoQuestion(
                id = 4,
                questionText = "What does a flashing YELLOW traffic light indicate at an intersection?",
                options = listOf(
                    "Stop immediately",
                    "Slow down, look both ways, and proceed with caution",
                    "Drive at maximum speed",
                    "The traffic light is broken"
                ),
                explanation = "Flashing yellow light warns drivers to reduce speed and cross the junction with extra alertness.",
                hiQuestionText = "चमकती हुई पीली बत्ती (Flashing Yellow) का क्या मतलब है?",
                hiOptions = listOf("तुरंत रुकें", "धीमे हों, दोनों तरफ देखें और सावधानी से आगे बढ़ें", "तेजी से पार करें", "बत्ती खराब है"),
                hiExplanation = "चमकती पीली बत्ती का मतलब है कि गति धीमी करें और सावधानीपूर्वक चौराहा पार करें।",
                bnQuestionText = "ঝলকানি দেওয়া হলুদ ট্রাফিক বাতির অর্থ কী?",
                bnOptions = listOf("থামুন", "গতি কমান, দুইপাশে দেখুন এবং সতর্কতার সাথে চলুন", "দ্রুত পার হন", "সিগন্যাল নষ্ট"),
                bnExplanation = "হলুদ আলো চালককে সতর্ক করে সাবধানে রাস্তা পার হতে নির্দেশ দেয়।",
                correctAnswerIndex = 1,
                category = "Traffic Lights",
                icon = "⚠️"
            ),
            RtoQuestion(
                id = 5,
                questionText = "When parking on a steep downhill incline with a curb, in which direction should you turn your steering wheel?",
                options = listOf(
                    "Towards the curb (Right if parked on left curb)",
                    "Straight ahead",
                    "Away from the curb",
                    "It does not matter"
                ),
                explanation = "Turning wheels towards the curb prevents the vehicle from rolling into traffic if the handbrake slips on a downhill slope.",
                hiQuestionText = "ढलान वाली सड़क पर पार्क करते समय पहियों को किस दिशा में मोड़ना चाहिए?",
                hiOptions = listOf("फुटपाथ (कर्ब) की तरफ", "एकदम सीधा", "फुटपाथ से दूर", "कोई फर्क नहीं पड़ता"),
                hiExplanation = "पहियों को कर्ब की ओर मोड़ने से ब्रेक फेल होने पर भी गाड़ी लुढ़कने से बचती है।",
                bnQuestionText = "ঢালু রাস্তায় গাড়ি পার্ক করার সময় চাকা কোন দিকে ঘোরানো উচিত?",
                bnOptions = listOf("ফুটপাতের (কার্ব) দিকে", "সোজা রেখে", "ফুটপাত থেকে দূরে", "কোন ব্যাপার না"),
                bnExplanation = "ফুটপাতের দিকে চাকা রাখলে ব্রেক কাজ না করলেও গাড়ি গড়িয়ে পড়বে না।",
                correctAnswerIndex = 0,
                category = "Parking Mastery",
                icon = "🅿️"
            ),
            RtoQuestion(
                id = 6,
                questionText = "What is the maximum permissible blood alcohol concentration (BAC) limit for drivers in India?",
                options = listOf(
                    "30 mg per 100 ml of blood",
                    "100 mg per 100 ml of blood",
                    "50 mg per 100 ml of blood",
                    "No limit"
                ),
                explanation = "Section 185 of Motor Vehicles Act sets the limit at 30mg per 100ml blood. Driving beyond this is a severe punishable offense.",
                hiQuestionText = "भारत में वाहन चालकों के लिए शराब की अधिकतम स्वीकार्य सीमा क्या है?",
                hiOptions = listOf("30 मिलीग्राम प्रति 100 मिली रक्त", "100 मिलीग्राम प्रति 100 मिली", "50 मिलीग्राम प्रति 100 मिली", "कोई सीमा नहीं"),
                hiExplanation = "मोटर वाहन अधिनियम धारा 185 के तहत 30 मिलीग्राम से अधिक शराब पीना दंडनीय अपराध है।",
                bnQuestionText = "ভারতে গাড়ি চালকদের রক্তে অ্যালকোহলের সর্বোচ্চ অনুমোদিত সীমা কত?",
                bnOptions = listOf("প্রতি ১০০ মিলি রক্তে ৩০ মিলিগ্রাম", "১০০ মিলিগ্রাম", "৫০ মিলিগ্রাম", "কোন সীমা নেই"),
                bnExplanation = "মোটর ভেহিকেল আইনের ১৮৫ ধারা অনুযায়ী ৩০ মিলিগ্রামের বেশি থাকা শাস্তিযোগ্য অপরাধ।",
                correctAnswerIndex = 0,
                category = "RTO Laws",
                icon = "⚖️"
            )
        )
    }

    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    var selectedOptionIdx by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var isQuizCompleted by remember { mutableStateOf(false) }

    val currentQ = questions[currentQuestionIdx]

    val activeQuestion = when (selectedLanguage) {
        RtoQuizLanguage.ENGLISH -> currentQ.questionText
        RtoQuizLanguage.HINDI -> currentQ.hiQuestionText
        RtoQuizLanguage.BENGALI -> currentQ.bnQuestionText
    }

    val activeOptions = when (selectedLanguage) {
        RtoQuizLanguage.ENGLISH -> currentQ.options
        RtoQuizLanguage.HINDI -> currentQ.hiOptions
        RtoQuizLanguage.BENGALI -> currentQ.bnOptions
    }

    val activeExplanation = when (selectedLanguage) {
        RtoQuizLanguage.ENGLISH -> currentQ.explanation
        RtoQuizLanguage.HINDI -> currentQ.hiExplanation
        RtoQuizLanguage.BENGALI -> currentQ.bnExplanation
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rto_quiz_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // HEADER WITH LANGUAGE TOGGLE & VOICE READ BUTTON
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ImperialGold.copy(alpha = 0.2f))
                            .border(1.dp, ImperialGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(currentQ.icon, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "RTO Driving License Exam",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Question ${currentQuestionIdx + 1} of ${questions.size}",
                            fontSize = 10.sp,
                            color = ImperialGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = {
                        speakText("$activeQuestion. Options: ${activeOptions.joinToString(", ")}", selectedLanguage)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(ImperialGold.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, ImperialGold.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen Question", tint = ImperialGold, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Language Selector Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                RtoQuizLanguage.values().forEach { lang ->
                    FilterChip(
                        selected = selectedLanguage == lang,
                        onClick = { selectedLanguage = lang },
                        label = { Text(lang.label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ImperialGold,
                            selectedLabelColor = ObsidianBlack,
                            containerColor = DarkSurfaceVariant,
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isQuizCompleted) {
                // QUESTION CARD
                Surface(
                    color = MidnightNavy,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Category: ${currentQ.category}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = activeQuestion,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // OPTIONS LIST
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    activeOptions.forEachIndexed { idx, optionText ->
                        val isSelected = selectedOptionIdx == idx
                        val isCorrect = idx == currentQ.correctAnswerIndex

                        val bgColor = when {
                            isAnswerSubmitted && isCorrect -> SuccessGreen.copy(alpha = 0.25f)
                            isAnswerSubmitted && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.25f)
                            isSelected -> ImperialGold.copy(alpha = 0.25f)
                            else -> DarkSurfaceVariant
                        }

                        val borderColor = when {
                            isAnswerSubmitted && isCorrect -> SuccessGreen
                            isAnswerSubmitted && isSelected && !isCorrect -> Color.Red
                            isSelected -> ImperialGold
                            else -> Color.Gray.copy(alpha = 0.3f)
                        }

                        Surface(
                            onClick = {
                                if (!isAnswerSubmitted) {
                                    selectedOptionIdx = idx
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = bgColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("rto_quiz_option_$idx")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) ImperialGold else Color.Transparent)
                                        .border(1.dp, if (isSelected) ImperialGold else Color.Gray, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ('A' + idx).toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ObsidianBlack else Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = optionText,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SUBMIT OR NEXT ACTION
                if (!isAnswerSubmitted) {
                    ThreeDButton(
                        onClick = {
                            if (selectedOptionIdx != null) {
                                isAnswerSubmitted = true
                                if (selectedOptionIdx == currentQ.correctAnswerIndex) {
                                    score++
                                    speakText("Correct answer! $activeExplanation", selectedLanguage)
                                } else {
                                    speakText("Incorrect. Correct answer is ${activeOptions[currentQ.correctAnswerIndex]}. $activeExplanation", selectedLanguage)
                                }
                            }
                        },
                        text = "Submit Answer",
                        icon = Icons.Default.CheckCircle,
                        containerColor = ImperialGold,
                        contentColor = ObsidianBlack,
                        shadowColor = WarmBronze,
                        enabled = selectedOptionIdx != null,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_submit_rto_answer"
                    )
                } else {
                    // EXPLANATION & NEXT BUTTON
                    Surface(
                        color = if (selectedOptionIdx == currentQ.correctAnswerIndex) SuccessGreen.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedOptionIdx == currentQ.correctAnswerIndex) SuccessGreen else Color.Red
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (selectedOptionIdx == currentQ.correctAnswerIndex) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (selectedOptionIdx == currentQ.correctAnswerIndex) SuccessGreen else Color.Red,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (selectedOptionIdx == currentQ.correctAnswerIndex) "CORRECT ANSWER!" else "INCORRECT",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = if (selectedOptionIdx == currentQ.correctAnswerIndex) SuccessGreen else Color.Red
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = activeExplanation,
                                fontSize = 11.sp,
                                color = ChampagneGold,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ThreeDButton(
                        onClick = {
                            if (currentQuestionIdx < questions.size - 1) {
                                currentQuestionIdx++
                                selectedOptionIdx = null
                                isAnswerSubmitted = false
                            } else {
                                isQuizCompleted = true
                            }
                        },
                        text = if (currentQuestionIdx < questions.size - 1) "Next Question" else "View Exam Results",
                        icon = Icons.Default.ArrowForward,
                        containerColor = ImperialGold,
                        contentColor = ObsidianBlack,
                        shadowColor = WarmBronze,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_next_rto_question"
                    )
                }

            } else {
                // QUIZ RESULT SUMMARY
                val passPercentage = (score.toFloat() / questions.size) * 100
                val isPassed = passPercentage >= 60

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isPassed) SuccessGreen.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f))
                            .border(2.dp, if (isPassed) SuccessGreen else Color.Red, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPassed) Icons.Default.EmojiEvents else Icons.Default.Replay,
                            contentDescription = null,
                            tint = if (isPassed) SuccessGreen else Color.Red,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = if (isPassed) "CONGRATULATIONS! RTO TEST PASSED" else "NEEDS MORE PRACTICE",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = if (isPassed) SuccessGreen else Color.Red
                    )

                    Surface(
                        color = MidnightNavy,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Your Score", fontSize = 11.sp, color = BrightGold)
                            Text("$score / ${questions.size}", fontWeight = FontWeight.Black, fontSize = 28.sp, color = ImperialGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isPassed) "You meet the official Indian RTO Learner's License theory passing standard." else "Review traffic rules and try again.",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }

                    ThreeDButton(
                        onClick = {
                            currentQuestionIdx = 0
                            selectedOptionIdx = null
                            score = 0
                            isAnswerSubmitted = false
                            isQuizCompleted = false
                        },
                        text = "Restart RTO Exam",
                        icon = Icons.Default.Refresh,
                        containerColor = ImperialGold,
                        contentColor = ObsidianBlack,
                        shadowColor = WarmBronze,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_restart_rto_quiz"
                    )
                }
            }
        }
    }
}
