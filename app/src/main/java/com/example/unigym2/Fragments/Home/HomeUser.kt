package com.example.unigym2.Fragments.Home

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [HomeUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeUser : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var titleView: TextView
    lateinit var communicator: Communicator
    lateinit var button: TextView

    lateinit var sessionTime : TextView
    lateinit var sessionPersonal : TextView
    lateinit var completedSessions : TextView
    lateinit var dayStreak : TextView
    lateinit var streakBar : ProgressBar
    lateinit var layoutButton : RelativeLayout

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        communicator.showLoadingOverlay()
        checkAndUpdateDayStreak()
        loadUserData()
        checkForNewAcceptedSessions()
        loadNextSession()

        communicator.hideLoadingOverlay()

    }

    private fun loadUserData() {
        db.collection("Usuarios").document(communicator.getAuthUser())
            .get()
            .addOnSuccessListener { result ->
                val userName = result.data?.get("name").toString()
                handleWeeklyReset(result)

                titleView.text = userName
                communicator.setAuthUserName(userName)
                dayStreak.text = "${result.data?.get("treinosConsecutivos")} Dias"
                Log.d("firestore", "streakBar progress set to: ${result.data?.get("treinosSemana")}")
                Log.d("firestore", "Collected data")
            }.addOnFailureListener { exception ->
                Log.w("firestore", "Error getting document.", exception)
            }
    }

    private fun handleWeeklyReset(result: DocumentSnapshot) {
        val lastResetTimestamp = result.getLong("lastSessionReset") ?: 0L
        val lastSunday = getLastSundayTimestamp()

        if (System.currentTimeMillis() > lastSunday && lastResetTimestamp < lastSunday) {
            resetWeeklyWorkouts()
        } else {
            updateWorkoutUI(result)
        }
    }

    private fun getLastSundayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysToSubtract = (currentDayOfWeek - Calendar.SUNDAY + 7) % 7

        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)

        return calendar.timeInMillis
    }

    private fun resetWeeklyWorkouts() {
        db.collection("Usuarios").document(communicator.getAuthUser())
            .update("treinosSemana", 0, "lastSessionReset", System.currentTimeMillis())
            .addOnSuccessListener {
                Log.d("firestore", "Streak reset successfully")
                streakBar.progress = 0
                completedSessions.text = "0 Completos"
            }
            .addOnFailureListener { e ->
                Log.w("firestore", "Error resetting streak", e)
            }
    }

    private fun updateWorkoutUI(result: DocumentSnapshot) {
        val weeklyWorkouts = result.getLong("treinosSemana")?.toInt() ?: 0
        streakBar.progress = weeklyWorkouts
        completedSessions.text = "$weeklyWorkouts Completos"
    }

    private fun checkForNewAcceptedSessions() {
        db.collection("Agendamentos")
            .whereEqualTo("clienteID", communicator.getAuthUser())
            .whereEqualTo("status", "aceito")
            .whereEqualTo("notificado", false)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val dataTreino = document.getString("data")
                    val horaTreino = document.getString("hora")
                    notifyAcceptedSession(document, dataTreino, horaTreino)
                }
            }
    }

    private fun notifyAcceptedSession(document: DocumentSnapshot, dataTreino: String?, horaTreino: String?) {
        db.collection("Usuarios")
            .document(document.get("personalID").toString())
            .get()
            .addOnSuccessListener { personalDoc ->
                document.reference.update("notificado", true)
                val personalName = personalDoc.get("name").toString()
                Toast.makeText(
                    requireContext(),
                    "Treino com $personalName no dia $dataTreino as $horaTreino aceito.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadNextSession() {
        val nowCalendar = Calendar.getInstance()
        val endOfTodayMillis = getEndOfDayTimestamp()

        db.collection("Agendamentos")
            .whereEqualTo("clienteID", communicator.getAuthUser())
            .whereEqualTo("status", "aceito")
            .whereGreaterThanOrEqualTo("timestamp", nowCalendar.timeInMillis)
            .whereLessThanOrEqualTo("timestamp", endOfTodayMillis)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    displayNextSession(querySnapshot.documents[0])
                } else {
                    displayNoSessions()
                }
                communicator.hideLoadingOverlay()
            }.addOnFailureListener { exception ->
                Log.w("firestore", "Erro ao buscar agendamentos do dia.", exception)
                sessionTime.text = "Erro ao carregar treinos"
                sessionPersonal.text = ""
                communicator.hideLoadingOverlay()
            }
    }

    private fun getEndOfDayTimestamp(): Long {
        val endOfTodayCalendar = Calendar.getInstance()
        endOfTodayCalendar.set(Calendar.HOUR_OF_DAY, 23)
        endOfTodayCalendar.set(Calendar.MINUTE, 59)
        endOfTodayCalendar.set(Calendar.SECOND, 59)
        endOfTodayCalendar.set(Calendar.MILLISECOND, 999)
        return endOfTodayCalendar.timeInMillis
    }

    private fun displayNextSession(document: DocumentSnapshot) {
        val sessionData = document.data
        displaySessionTime(document)
        displayPersonalName(sessionData)
    }

    private fun displaySessionTime(document: DocumentSnapshot) {
        val sessionTimestamp = document.getLong("timestamp")
        if (sessionTimestamp != null) {
            val sessionCal = Calendar.getInstance()
            sessionCal.timeInMillis = sessionTimestamp
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            sessionTime.text = timeFormatter.format(sessionCal.time)
        } else {
            sessionTime.text = document.getString("hora") ?: "N/A"
        }
    }

    private fun displayPersonalName(sessionData: Map<String, Any>?) {
        val personalId = sessionData?.get("personalID") as? String
        if (!personalId.isNullOrEmpty()) {
            db.collection("Usuarios").document(personalId)
                .get()
                .addOnSuccessListener { personalResult ->
                    if (personalResult.exists()) {
                        sessionPersonal.text = personalResult.getString("name") ?: "Personal"
                    } else {
                        sessionPersonal.text = "Personal não encontrado"
                    }
                    sessionTime.visibility = View.VISIBLE
                    sessionPersonal.visibility = View.VISIBLE
                }.addOnFailureListener { exception ->
                    Log.w("firestore", "Erro ao buscar dados do personal.", exception)
                    sessionPersonal.text = "Erro Personal"
                    sessionTime.visibility = View.VISIBLE
                    sessionPersonal.visibility = View.VISIBLE
                }
        } else {
            sessionPersonal.text = "Personal não informado"
            sessionTime.visibility = View.VISIBLE
            sessionPersonal.visibility = View.VISIBLE
        }
    }

    private fun displayNoSessions() {
        sessionTime.text = "Nenhum treino hoje"
        sessionPersonal.text = ""
    }

    private fun checkAndUpdateDayStreak() {
        val userId = communicator.getAuthUser()
        val userRef = db.collection("Usuarios").document(userId)

        userRef.get().addOnSuccessListener { document ->
            val lastWorkoutTimestamp = document.getLong("lastWorkoutTimestamp") ?: 0
            val currentStreak = document.getLong("treinosConsecutivos")?.toInt() ?: 0
            val currentTime = System.currentTimeMillis()

            // Get calendar instances for current time and last workout
            val currentCalendar = Calendar.getInstance()
            currentCalendar.timeInMillis = currentTime

            val lastWorkoutCalendar = Calendar.getInstance()
            lastWorkoutCalendar.timeInMillis = lastWorkoutTimestamp

            // Calculate the difference in days
            val daysSinceLastWorkout = getDaysBetween(lastWorkoutCalendar, currentCalendar)

            // Check if streak should be reset
            var newStreak = currentStreak
            var shouldReset = false

            if (daysSinceLastWorkout > 3) {
                // If more than 3 days have passed, definitely reset (longest weekend is 3 days)
                shouldReset = true
            } else if (daysSinceLastWorkout > 0) {
                // Check if we missed a weekday workout
                val missedWeekday = checkIfMissedWeekday(lastWorkoutCalendar, currentCalendar)
                if (missedWeekday) {
                    shouldReset = true
                }
            }

            // Update streak in Firebase if needed
            if (shouldReset) {
                userRef.update(
                    mapOf(
                        "treinosConsecutivos" to 0
                    )
                ).addOnSuccessListener {
                    Log.d("firestore", "Day streak reset successfully")
                    dayStreak.text = "0 Dias"
                }
            }
        }
    }

    // Helper function to check if a weekday workout was missed
    private fun checkIfMissedWeekday(lastWorkout: Calendar, current: Calendar): Boolean {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.timeInMillis = lastWorkout.timeInMillis

        while (tempCalendar.before(current)) {
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)

            val dayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)

            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && !isSameDay(tempCalendar, current)) {
                return true
            }
        }

        return false
    }

    // Helper function to calculate days between two dates
    private fun getDaysBetween(startDate: Calendar, endDate: Calendar): Int {
        val startDay = startDate.get(Calendar.DAY_OF_YEAR)
        val startYear = startDate.get(Calendar.YEAR)
        val endDay = endDate.get(Calendar.DAY_OF_YEAR)
        val endYear = endDate.get(Calendar.YEAR)

        return if (startYear == endYear) {
            endDay - startDay
        } else {
            var result = 0
            var tempYear = startYear

            // Add days remaining in start year
            val startYearDays = if (startYear % 4 == 0) 366 else 365
            result += startYearDays - startDay

            // Add days for years in between
            tempYear++
            while (tempYear < endYear) {
                result += if (tempYear % 4 == 0) 366 else 365
                tempYear++
            }

            // Add days in end year
            result += endDay

            result
        }
    }

    // Helper function to check if two dates are the same day
    private fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)
    }

    private fun updateWorkoutCompleted() {
        val userId = communicator.getAuthUser()
        val userRef = db.collection("Usuarios").document(userId)

        userRef.get().addOnSuccessListener { document ->
            val currentStreak = document.getLong("treinosConsecutivos")?.toInt() ?: 0
            var weeklyWorkouts = document.getLong("treinosSemana")?.toInt() ?: 0
            if (document.getLong("lastWorkoutTimestamp") == null) {
                userRef.update("lastWorkoutTimestamp", 0L)
                Log.d("firestore", "lastWorkoutTimestamp was null, set to 0")
            } else {
                Log.d("firestore", "lastWorkoutTimestamp is already set")
            }
            if (weeklyWorkouts >= 6) {
                weeklyWorkouts -= 6
                document.reference.update("treinosSemana", weeklyWorkouts)
            }

            // Increment streak and weekly workout count
            val newStreak = currentStreak + 1
            val newWeeklyWorkouts = if (weeklyWorkouts < 6) weeklyWorkouts + 1 else 0

            userRef.update(
                mapOf(
                    "treinosConsecutivos" to newStreak,
                    "treinosSemana" to newWeeklyWorkouts,
                    "lastWorkoutTimestamp" to System.currentTimeMillis()
                )
            ).addOnSuccessListener {
                Log.d("firestore", "Streak updated successfully")
                dayStreak.text = "$newStreak Dias"
                completedSessions.text = "$newWeeklyWorkouts Completos"
                streakBar.progress = newWeeklyWorkouts
                checkButtonVisibility()
            }.addOnFailureListener { e ->
                Log.w("firestore", "Error updating streak", e)
            }
        }
    }

    private fun initializeViews(v: View) {
        titleView = v.findViewById(R.id.nameTitle)
        sessionTime = v.findViewById(R.id.nextSessionTime)
        sessionPersonal = v.findViewById(R.id.nextSessionCostumer)
        completedSessions = v.findViewById(R.id.weeklyCompletedSessions)
        dayStreak = v.findViewById(R.id.dayStreak)
        streakBar = v.findViewById(R.id.progressBar)
        button = v.findViewById(R.id.treinoFeitoButton)
        layoutButton = v.findViewById(R.id.treinoFeitoLayout)
        communicator = activity as Communicator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_home_user, container, false)
        initializeViews(v)
        setupWorkoutButton()

        return v
    }

    private fun setupWorkoutButton() {
        communicator.showLoadingOverlay()
        checkButtonVisibility()

        button.setOnClickListener {
            handleWorkoutButtonClick()
        }
    }

    private fun checkButtonVisibility() {
        db.collection("Usuarios").document(communicator.getAuthUser())
            .get()
            .addOnSuccessListener { document ->
                val lastWorkoutTimestamp = document.getLong("lastWorkoutTimestamp") ?: 0L
                if (lastWorkoutTimestamp == 0L ||
                    System.currentTimeMillis() - lastWorkoutTimestamp > 24 * 60 * 60 * 1000) {
                    layoutButton.visibility = View.VISIBLE
                } else {
                    layoutButton.visibility = View.GONE
                    Log.d("firestore", "Workout already completed today")
                }
            }
            .addOnFailureListener { e ->
                Log.w("firestore", "Error getting document.", e)
            }
    }

    private fun handleWorkoutButtonClick() {
        db.collection("Usuarios").document(communicator.getAuthUser())
            .get()
            .addOnSuccessListener { document ->
                val lastWorkoutTimestamp = document.getLong("lastWorkoutTimestamp") ?: 0L
                if (lastWorkoutTimestamp == 0L ||
                    System.currentTimeMillis() - lastWorkoutTimestamp > 24 * 60 * 60 * 1000) {
                    updateWorkoutCompleted()
                }
            }
            .addOnFailureListener { e ->
                Log.w("firestore", "Error getting document.", e)
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}