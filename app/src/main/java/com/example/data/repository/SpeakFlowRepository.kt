package com.example.data.repository

import com.example.data.db.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class SpeakFlowRepository(
    private val dao: SpeakFlowDao
) {
    // Database flows
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val savedVocab: Flow<List<SavedVocabEntity>> = dao.getAllVocab()
    val practiceSessions: Flow<List<PracticeSessionEntity>> = dao.getAllSessions()
    val tutorBookings: Flow<List<TutorBookingEntity>> = dao.getAllTutorBookings()
    val badges: Flow<List<UserBadgeEntity>> = dao.getAllBadges()

    suspend fun updateProfile(profile: UserProfileEntity) = dao.insertOrUpdateProfile(profile)
    suspend fun addXPAndStats(xp: Int, fluency: Int, minutes: Int) = dao.addXPAndStats(xp, fluency, minutes)
    suspend fun saveVocab(vocab: SavedVocabEntity) = dao.insertVocab(vocab)
    suspend fun updateVocabSRS(id: Long, mastery: Int, interval: Int, nextReview: Long) = dao.updateVocabSRS(id, mastery, interval, nextReview)
    suspend fun deleteVocab(vocab: SavedVocabEntity) = dao.deleteVocab(vocab)
    suspend fun saveSession(session: PracticeSessionEntity) = dao.insertSession(session)
    suspend fun bookTutor(booking: TutorBookingEntity) = dao.insertTutorBooking(booking)
    suspend fun unlockBadge(id: String, date: String) = dao.unlockBadge(id, date)

    // Predefined AI Personas
    val personas = listOf(
        AIPersona(
            id = "alex_us",
            name = "Alex Vance",
            role = "Tech Lead & VP of Engineering",
            accent = "US",
            accentFlag = "🇺🇸",
            speechPitch = 1.0f,
            speechRate = 0.96f,
            avatarColorHex = 0xFF3B82F6,
            bio = "Silicon Valley veteran. Evaluates technical clarity, concise project delivery, and cross-functional leadership."
        ),
        AIPersona(
            id = "charlotte_uk",
            name = "Charlotte Kensington",
            role = "Executive Communications Director",
            accent = "UK",
            accentFlag = "🇬🇧",
            speechPitch = 1.05f,
            speechRate = 0.93f,
            avatarColorHex = 0xFF8B5CF6,
            bio = "London-based executive coach. Specializes in Received Pronunciation, diplomatic nuance, and boardroom persuasion."
        ),
        AIPersona(
            id = "liam_au",
            name = "Liam O'Connor",
            role = "Startup Founder & Product Strategist",
            accent = "AU",
            accentFlag = "🇦🇺",
            speechPitch = 0.98f,
            speechRate = 1.0f,
            avatarColorHex = 0xFF10B981,
            bio = "Sydney entrepreneur. Focuses on warm rapport-building, casual networking, and agile collaborative speech."
        ),
        AIPersona(
            id = "emma_ca",
            name = "Dr. Emma Laurent",
            role = "Chief Medical Consultant",
            accent = "CA",
            accentFlag = "🇨🇦",
            speechPitch = 1.02f,
            speechRate = 0.95f,
            avatarColorHex = 0xFF06B6D4,
            bio = "Toronto clinical researcher. Expert in empathetic patient counseling, clear scientific articulation, and active listening."
        )
    )

    // Predefined Scenarios
    val scenarios = listOf(
        RoleplayScenario(
            id = "sc_tech_interview",
            title = "Senior Tech Lead Job Interview",
            category = ScenarioCategory.CAREER,
            targetCEFR = CEFRLevel.B2,
            description = "Navigate a high-stakes behavioral and system design interview with VP Alex. Articulate architectural tradeoffs and team leadership.",
            initialMessage = "Hello and welcome! Thanks for joining today's final round. To start, could you walk me through a major system architecture challenge you led, and how you handled cross-team alignment?",
            aiPersona = personas[0],
            contextObjectives = listOf("Explain trade-offs clearly", "Use STAR method", "Adopt confident cadence"),
            suggestedKeywords = listOf("scalability", "bottleneck", "stakeholder buy-in", "trade-offs", "resilience")
        ),
        RoleplayScenario(
            id = "sc_salary_negotiation",
            title = "Executive Salary & Equity Negotiation",
            category = ScenarioCategory.NEGOTIATION,
            targetCEFR = CEFRLevel.C1,
            description = "Negotiate a compensation package, base salary increase, and equity refresher using polite yet assertive diplomatic English.",
            initialMessage = "Good morning! We're thrilled by your performance and would love to formalize our offer with a base of $145,000. How does that align with your expectations?",
            aiPersona = personas[1],
            contextObjectives = listOf("Anchor value proposition", "Polite counter-offer", "Avoid aggressive idioms"),
            suggestedKeywords = listOf("market benchmark", "total comp", "equity vesting", "scope of impact", "counter-proposal")
        ),
        RoleplayScenario(
            id = "sc_airport_customs",
            title = "International Border & Customs Transit",
            category = ScenarioCategory.TRAVEL,
            targetCEFR = CEFRLevel.B1,
            description = "Answer border control officers clearly regarding your travel itinerary, visa duration, and declared items.",
            initialMessage = "Passports please. What is the primary purpose of your visit to London, and how long do you intend to stay?",
            aiPersona = personas[1],
            contextObjectives = listOf("Direct succinct answers", "State dates clearly", "Formal courteous tone"),
            suggestedKeywords = listOf("itinerary", "accommodation", "transit visa", "conference attendance", "return flight")
        ),
        RoleplayScenario(
            id = "sc_doctor_visit",
            title = "Medical Clinic Consultation & Diagnosis",
            category = ScenarioCategory.HEALTH,
            targetCEFR = CEFRLevel.B1,
            description = "Describe your physical symptoms, onset timeline, pain severity, and ask about side effects and treatment plans.",
            initialMessage = "Good afternoon. I see you booked an appointment for recurring chest tightness and fatigue. When did you first notice these symptoms?",
            aiPersona = personas[3],
            contextObjectives = listOf("Describe pain qualities (sharp/dull)", "Explain onset timeline", "Ask medication dosage"),
            suggestedKeywords = listOf("symptoms", "prescribed", "throbbing pain", "side effects", "dosage")
        ),
        RoleplayScenario(
            id = "sc_networking_social",
            title = "Tech Mixer & Elevator Pitch Networking",
            category = ScenarioCategory.DAILY_LIFE,
            targetCEFR = CEFRLevel.B1,
            description = "Break the ice at an industry mixer, share your 30-second elevator pitch, and exchange LinkedIn connections.",
            initialMessage = "Hey there! Great keynote talk on AI agents, wasn't it? Have you been following the recent developments in real-time speech synthesis?",
            aiPersona = personas[2],
            contextObjectives = listOf("Warm icebreaker", "Deliver 30s pitch", "Graceful contact exchange"),
            suggestedKeywords = listOf("connect on LinkedIn", "fascinating perspective", "innovative product", "touch base")
        ),
        RoleplayScenario(
            id = "sc_apartment_lease",
            title = "Apartment Viewing & Lease Agreement",
            category = ScenarioCategory.DAILY_LIFE,
            targetCEFR = CEFRLevel.B2,
            description = "Tour an apartment, inquire about maintenance policies, utility fees, security deposit, and negotiate lease clauses.",
            initialMessage = "Welcome to the 2-bedroom unit on 4th Avenue. As you can see, it has floor-to-ceiling windows and central heating. What questions do you have before we review the lease?",
            aiPersona = personas[0],
            contextObjectives = listOf("Ask about hidden fees", "Clarify pet/sublet policy", "Negotiate move-in date"),
            suggestedKeywords = listOf("security deposit", "utilities included", "sublet clause", "move-in inspection")
        )
    )

    // Core Phonemes for 2D Articulation Visualizer
    val phonemes = listOf(
        PhonemeArticulation(
            ipa = "/θ/",
            soundName = "Voiceless Interdental Fricative",
            exampleWords = listOf("Think", "Thought", "Breath", "Method"),
            mouthShape = MouthShape.OPEN_RELAXED,
            tonguePosition = TonguePosition.INTERDENTAL,
            isVoiced = false,
            airflowType = AirflowType.CONTINUOUS_FRICATIVE,
            tips = "Gently place tongue tip between upper and lower front teeth. Blow a steady stream of air without vibrating vocal cords.",
            commonMistakes = "Do not substitute with /s/ ('sink') or /f/ ('fink'). Keep the tongue visibly between your teeth."
        ),
        PhonemeArticulation(
            ipa = "/ð/",
            soundName = "Voiced Interdental Fricative",
            exampleWords = listOf("This", "That", "Breathe", "Mother"),
            mouthShape = MouthShape.OPEN_RELAXED,
            tonguePosition = TonguePosition.INTERDENTAL,
            isVoiced = true,
            airflowType = AirflowType.CONTINUOUS_FRICATIVE,
            tips = "Same tongue position as /θ/, but turn on your vocal cord vibration (feel your throat buzz).",
            commonMistakes = "Do not replace with /z/ ('zis') or /d/ ('dis')."
        ),
        PhonemeArticulation(
            ipa = "/r/",
            soundName = "American Postalveolar / Retroflex Approximant",
            exampleWords = listOf("Red", "Right", "Car", "Mirror"),
            mouthShape = MouthShape.ROUND_OPEN,
            tonguePosition = TonguePosition.RETROFLEX_CURL,
            isVoiced = true,
            airflowType = AirflowType.LIQUID_FLOW,
            tips = "Curl the tongue tip slightly back toward the roof of your mouth WITHOUT touching it. Flare the lips slightly.",
            commonMistakes = "Never roll or flap the tongue like Spanish/Italian 'r'. The tongue must not touch the roof."
        ),
        PhonemeArticulation(
            ipa = "/l/",
            soundName = "Alveolar Lateral Approximant (Light & Dark L)",
            exampleWords = listOf("Light", "Love", "Call", "Bottle"),
            mouthShape = MouthShape.OPEN_RELAXED,
            tonguePosition = TonguePosition.ALVEOLAR_RIDGE,
            isVoiced = true,
            airflowType = AirflowType.LIQUID_FLOW,
            tips = "Firmly press tongue tip against the ridge behind your upper front teeth. Air flows around the sides.",
            commonMistakes = "Do not confuse with /r/ or /w/. Maintain clean alveolar contact for light L."
        ),
        PhonemeArticulation(
            ipa = "/æ/",
            soundName = "Near-Open Front Unrounded Vowel ('Short A')",
            exampleWords = listOf("Cat", "Black", "Action", "Apple"),
            mouthShape = MouthShape.OPEN_RELAXED,
            tonguePosition = TonguePosition.LOW_FRONT,
            isVoiced = true,
            airflowType = AirflowType.VOWEL_FREE,
            tips = "Drop your jaw wide down. The tongue rests flat and low in the front of your mouth.",
            commonMistakes = "Do not confuse with /e/ ('bed') or /ʌ/ ('cut'). Cat ≠ Bet ≠ Cut."
        ),
        PhonemeArticulation(
            ipa = "/ə/",
            soundName = "Schwa (The Most Common English Sound)",
            exampleWords = listOf("About", "Banana", "Doctor", "Pencil"),
            mouthShape = MouthShape.NEUTRAL_REST,
            tonguePosition = TonguePosition.ALVEOLAR_RIDGE,
            isVoiced = true,
            airflowType = AirflowType.VOWEL_FREE,
            tips = "Complete relaxation. Minimal lip opening, tongue totally neutral and unstressed.",
            commonMistakes = "Do not over-enunciate unstressed vowels into full vowels."
        ),
        PhonemeArticulation(
            ipa = "/ʃ/",
            soundName = "Voiceless Postalveolar Fricative",
            exampleWords = listOf("Ship", "Ocean", "Nation", "Wish"),
            mouthShape = MouthShape.ROUND_OPEN,
            tonguePosition = TonguePosition.PALATAL,
            isVoiced = false,
            airflowType = AirflowType.CONTINUOUS_FRICATIVE,
            tips = "Round your lips forward in a slight trumpet shape. Tongue arches near the hard palate.",
            commonMistakes = "Do not flatten lips like /s/. Keep air rushing through a wide channel."
        ),
        PhonemeArticulation(
            ipa = "/v/",
            soundName = "Voiced Labiodental Fricative",
            exampleWords = listOf("Voice", "Very", "Travel", "Move"),
            mouthShape = MouthShape.LIP_TEETH_CONTACT,
            tonguePosition = TonguePosition.LOW_FRONT,
            isVoiced = true,
            airflowType = AirflowType.CONTINUOUS_FRICATIVE,
            tips = "Top front teeth rest lightly on inside edge of bottom lip. Vibrate vocal cords as air passes.",
            commonMistakes = "Do not use two rounded lips like /w/ ('wery' for 'very') or substitute /b/ ('bery')."
        )
    )

    // Placement Test Questions
    val placementQuestions = listOf(
        PlacementQuestion(
            id = 1,
            title = "Introduction & Background",
            prompt = "Introduce yourself, your current professional or academic focus, and your primary motivation for mastering spoken English.",
            subPrompt = "Focus on natural conversational pace, past/present tense consistency, and vocabulary variety.",
            focusArea = "Fluency & Intro",
            speakingSeconds = 45
        ),
        PlacementQuestion(
            id = 2,
            title = "Complex Scenario Description",
            prompt = "Describe a challenging problem you solved recently at work, school, or in daily life. What was the conflict and how did you resolve it?",
            subPrompt = "Demonstrate narrative sequencing (firstly, subsequently, ultimately) and causal grammar structures.",
            focusArea = "Descriptive Grammar",
            speakingSeconds = 45
        ),
        PlacementQuestion(
            id = 3,
            title = "Argumentation & Opinion Debate",
            prompt = "Should companies transition permanently to fully remote work, or is in-person collaboration indispensable? Give reasons for your stance.",
            subPrompt = "Use nuanced modal verbs (might, could arguably, would suggest) and balanced discourse markers.",
            focusArea = "Argumentation & Discourse",
            speakingSeconds = 45
        ),
        PlacementQuestion(
            id = 4,
            title = "Hypothetical Scenario & Nuance",
            prompt = "If you were given $1,000,000 to launch an initiative that directly impacts global education, what would you build and why?",
            subPrompt = "Demonstrate conditional structures (If I were to... I would prioritize...) and idiomatic precision.",
            focusArea = "Idioms & Complex Syntax",
            speakingSeconds = 45
        ),
        PlacementQuestion(
            id = 5,
            title = "Phonetics & Stress Articulation Challenge",
            prompt = "Read aloud clearly: 'Although the innovative architectural methodology seemed thoroughly overwhelming, their enthusiastic collaboration produced phenomenal breakthroughs.'",
            subPrompt = "Focus on word stress, clear interdentals /θ/, /ð/, and fluent consonant linking.",
            focusArea = "Phonetics & Intonation",
            speakingSeconds = 30
        )
    )

    // Micro-Lessons Catalog
    val microLessons = listOf(
        MicroLesson(
            id = "ml_phrasal_business",
            title = "High-Impact Business Phrasal Verbs",
            category = "Business Jargon",
            targetCEFR = CEFRLevel.B2,
            overview = "Elevate boardroom communication by substituting generic verbs with natural, high-frequency phrasal verbs.",
            formulaOrRule = "Action verb + Particle (up, out, down, across) creates specific idiomatic meaning.",
            exampleDialogues = listOf(
                DialoguePair("Manager", "Can you explain the main conclusions to the executives?", "Lead", "I'll walk them through the key findings.", "walk them through", "Explains step-by-step guidance in a meeting."),
                DialoguePair("Colleague", "We need to delay the product launch by two weeks.", "Lead", "Understood, let's push back the release date.", "push back", "Professional way to say postpone or reschedule.")
            ),
            shadowDrills = listOf(
                ShadowDrill("Let me walk you through our quarterly roadmap.", "/lɛt miː wɔːk juː θruː/", "Stress on 'WALK' and 'ROADMAP'", "Connect 'walk' and 'you' smoothly."),
                ShadowDrill("We decided to push back the deadline to Friday.", "/pʊʃ bæk ðə ˈdɛdlaɪn/", "Stress on 'BACK' and 'DEADLINE'", "Release the /ʃ/ softly into /b/.")
            ),
            quickQuiz = listOf(
                QuizQuestion(
                    prompt = "Which phrasal verb means 'to mention or introduce a topic during a meeting'?",
                    options = listOf("Bring up", "Break down", "Call out", "Take in"),
                    correctIndex = 0,
                    explanation = "'Bring up' means to initiate discussion on a subject."
                )
            )
        ),
        MicroLesson(
            id = "ml_accent_reduction_schwa",
            title = "Mastering the Schwa /ə/ for Native Rhythm",
            category = "Accent Reduction",
            targetCEFR = CEFRLevel.B1,
            overview = "The Schwa /ə/ is the secret to natural English cadence. Reduce unstressed syllables to speak with effortless rhythm.",
            formulaOrRule = "In multi-syllable English words, unstressed vowels turn into /ə/ instead of their written alphabet sound.",
            exampleDialogues = listOf(
                DialoguePair("Learner", "Is it PHOH-TOH-GRAPH?", "Coach", "No, say /ˈfoʊ.tə.ɡræf/ — the middle 'o' is a tiny relaxed /ə/.", "foʊ.tə.ɡræf", "Unstressed 'o' collapses to /ə/."),
                DialoguePair("Learner", "And what about pho-TO-gra-phy?", "Coach", "Now stress shifts: /fəˈtɑː.ɡrə.fi/ — the first and third vowels become /ə/!", "fəˈtɑː.ɡrə.fi", "Notice how stress shifts the schwa position.")
            ),
            shadowDrills = listOf(
                ShadowDrill("I had a cup of coffee at the station.", "/aɪ hæd ə kʌp əv ˈkɔːfi ət ðə ˈsteɪʃən/", "Content words (CUP, COFFEE, STATION) stressed", "Make 'a', 'of', 'at', 'the' ultra short and light.")
            ),
            quickQuiz = listOf(
                QuizQuestion(
                    prompt = "In the word 'BANANA', which syllables contain the neutral Schwa /ə/ sound?",
                    options = listOf("The first and third syllables (bə-NA-nə)", "The middle syllable only", "All three syllables", "None"),
                    correctIndex = 0,
                    explanation = "'Banana' is pronounced /bəˈnæn.ə/, where only the middle 'a' is open /æ/."
                )
            )
        ),
        MicroLesson(
            id = "ml_diplomatic_negotiation",
            title = "Diplomatic Language & Polite Disagreement",
            category = "High-Stakes Talks",
            targetCEFR = CEFRLevel.C1,
            overview = "Learn how executive negotiators express disagreement without causing friction or defensiveness.",
            formulaOrRule = "Use softening qualifiers + perspective markers ('I see where you're coming from, however...')",
            exampleDialogues = listOf(
                DialoguePair("Partner", "This price is too high for our budget.", "Negotiator", "I hear your concern. If we look at the long-term ROI, however, it delivers significant cost savings.", "I hear your concern", "Validates the counterpart before offering an alternative."),
                DialoguePair("Client", "We need this done in 3 days.", "Lead", "I wish we could commit to that; to ensure quality, would early next week work?", "I wish we could", "Gentle boundary setting.")
            ),
            shadowDrills = listOf(
                ShadowDrill("With respect, that approach might introduce unforeseen friction.", "/wɪð rɪˈspɛkt ðæt əˈproʊtʃ maɪt ˌɪntrəˈduːs/", "Rising inflection on 'respect', falling on 'friction'", "Keep tone calm and even.")
            ),
            quickQuiz = listOf(
                QuizQuestion(
                    prompt = "What is the most diplomatic alternative to 'You are wrong about that'?",
                    options = listOf("I have a slightly different perspective on those numbers.", "Your data is incorrect.", "That makes no sense.", "You made an error."),
                    correctIndex = 0,
                    explanation = "Focusing on 'different perspective' depersonalizes the disagreement."
                )
            )
        )
    )

    // Peer Practice Topics
    val peerTopics = listOf(
        PeerPracticeTopic(
            id = "peer_ai_future",
            title = "Will AI Replace or Enhance Creative Professions?",
            category = "Technology & Society",
            durationSeconds = 300,
            icebreakers = listOf(
                "Have you used generative AI tools in your personal or work life?",
                "What human skill do you believe can never be automated by machines?"
            ),
            keyDebatePoints = listOf("Efficiency vs Authenticity", "Job market evolution", "Creative copyright"),
            recommendedIdioms = listOf("double-edged sword", "on the horizon", "think outside the box")
        ),
        PeerPracticeTopic(
            id = "peer_remote_culture",
            title = "Global Remote Work: Freedom vs Team Connection",
            category = "Work & Culture",
            durationSeconds = 300,
            icebreakers = listOf(
                "Would you prefer working from a beach in Bali or a lively city office?",
                "How do you maintain focus and avoid burnout when working from home?"
            ),
            keyDebatePoints = listOf("Work-life boundary", "Asynchronous collaboration", "Spontaneous watercooler innovation"),
            recommendedIdioms = listOf("strike a balance", "burn the candle at both ends", "get the ball rolling")
        ),
        PeerPracticeTopic(
            id = "peer_travel_culture",
            title = "The Most Transformative Travel Experience",
            category = "Travel & Culture",
            durationSeconds = 300,
            icebreakers = listOf(
                "What foreign culture or tradition surprised you the most?",
                "Share one funny miscommunication you had while traveling abroad."
            ),
            keyDebatePoints = listOf("Cultural immersion", "Language barriers", "Empathy development"),
            recommendedIdioms = listOf("out of one's comfort zone", "when in Rome", "broaden your horizons")
        )
    )

    // Tutors
    val tutors = listOf(
        TutorProfile(
            id = "tutor_sarah",
            name = "Sarah Jenkins, M.Ed.",
            title = "Cambridge Certified Accent & Executive Coach",
            accent = "General American 🇺🇸",
            rating = 4.98,
            reviewsCount = 420,
            specialty = "Accent Reduction & Pitch Intonation",
            hourlyRate = "$38/hr",
            avatarBgColor = 0xFF4F46E5,
            bio = "10+ years coaching global software engineers and executives at Google and Meta for keynote presentations."
        ),
        TutorProfile(
            id = "tutor_eleanor",
            name = "Dr. Eleanor Vance",
            title = "Oxford Applied Linguistics PhD",
            accent = "British RP 🇬🇧",
            rating = 4.95,
            reviewsCount = 310,
            specialty = "IELTS 8.5+ & Academic Discourse",
            hourlyRate = "$45/hr",
            avatarBgColor = 0xFF8B5CF6,
            bio = "Specialist in advanced idiom subtlety, formal argumentation, and phonetic articulation."
        ),
        TutorProfile(
            id = "tutor_liam_coach",
            name = "Liam O'Connor",
            title = "Startup Founder & Communication Trainer",
            accent = "Australian 🇦🇺",
            rating = 4.92,
            reviewsCount = 280,
            specialty = "Casual Fluency & Spontaneous Small Talk",
            hourlyRate = "$32/hr",
            avatarBgColor = 0xFF10B981,
            bio = "Master conversational confidence, overcome speaking anxiety, and enjoy rapid feedback loops."
        )
    )

    // Community Discussions
    val communityDiscussions = listOf(
        CommunityDiscussion(
            id = "comm_1",
            authorName = "Sofia Rossi",
            authorLevel = CEFRLevel.B2,
            authorAvatarColor = 0xFFEC4899,
            title = "🎙️ Daily Speech Challenge: 45-Second Elevator Pitch",
            transcript = "Hi everyone! Here is my pitch for a green energy startup. Any feedback on my /r/ pronunciation and word stress?",
            audioDurationSec = 45,
            upvotes = 38,
            repliesCount = 14,
            topicTag = "Pronunciation Drill",
            timeAgo = "2h ago"
        ),
        CommunityDiscussion(
            id = "comm_masterclass",
            authorName = "Sarah Jenkins (Tutor)",
            authorLevel = CEFRLevel.C2,
            authorAvatarColor = 0xFF4F46E5,
            title = "🔴 Live Masterclass: Overcoming the /θ/ vs /s/ and /r/ vs /l/ Confusion",
            transcript = "Join today's live interactive interactive workshop with 2D mouth camera breakdown and live student drills!",
            audioDurationSec = 0,
            upvotes = 112,
            repliesCount = 47,
            topicTag = "Live Masterclass",
            timeAgo = "Starts in 45m",
            isMasterclass = true,
            masterclassTime = "Today, 5:00 PM GMT"
        ),
        CommunityDiscussion(
            id = "comm_2",
            authorName = "Kenji Takahashi",
            authorLevel = CEFRLevel.B1,
            authorAvatarColor = 0xFF3B82F6,
            title = "💡 How I eliminated 90% of my 'um' and 'like' filler words in 2 weeks",
            transcript = "The Speech Lab filler counter in SpeakFlow was a gamechanger. Whenever I wanted to say 'um', I trained myself to take a silent breath instead.",
            audioDurationSec = 62,
            upvotes = 94,
            repliesCount = 29,
            topicTag = "Fluency Strategy",
            timeAgo = "5h ago"
        )
    )
}
