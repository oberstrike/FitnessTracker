package main.com.fitnesstracker.views.main


import io.reactivex.subjects.BehaviorSubject
import main.com.fitnesstracker.ext.convertDateTimeToHeadline
import main.com.fitnesstracker.models.Activeness
import main.com.fitnesstracker.models.ActivenessType
import main.com.fitnesstracker.repositories.ActivenessRepo
import main.com.fitnesstracker.repositories.ExerciseRepo
import main.com.fitnesstracker.repositories.WorkoutSetRepo
import main.com.fitnesstracker.util.MainContext
import main.com.fitnesstracker.util.rx.SchedulerProvider
import main.com.fitnesstracker.views.AbstractViewModel

class MainViewModel(
    private val activenessRepo: ActivenessRepo,
    private val exerciseRepo: ExerciseRepo,
    private val workoutSetRepo: WorkoutSetRepo,
    val provider: SchedulerProvider
                   ) : AbstractViewModel(){


    var page: BehaviorSubject<Int> = BehaviorSubject.create()
    var pages: BehaviorSubject<Int> = BehaviorSubject.create()

    init {
        MainContext.allActivenessObservable = activenessRepo.getAllActivenesses()
        page.onNext(0)
        launch {
            getAllActiveness().subscribe {
                pages.onNext((it.size / 10 + 1))
            }
        }

        launch {
            page.subscribe {
                val temp = MainContext.createActiveness()
                MainContext.settings.page = it
                activenessRepo.saveActiveness(temp)
                activenessRepo.delete(temp)
            }
        }
    }


    fun addActiveness(activenessType: ActivenessType) {
        val activeness = MainContext.createActiveness()
        with(activeness) {
            name = convertDateTimeToHeadline(date)
            type = activenessType
        }
        activenessRepo.saveActiveness(activeness)
    }

    fun deleteAllActiveness() {
        activenessRepo.deleteAll()
    }

    fun getAllActiveness() = MainContext.allActivenessObservable

    fun deleteActiveness(activeness: Activeness): Boolean {
        val exercises = activeness.exercises
        for (exercise in exercises) {
            val workoutSets = exercise.workoutSets
            for (y in workoutSets) {
                workoutSetRepo.delete(y)
            }
            exerciseRepo.deleteExercise(exercise)
        }

        activenessRepo.delete(activeness)
        return true
    }

    fun rename(activeness: Activeness, newName: String?): Boolean {
        if (newName != null) {
            if (newName.isNotEmpty()) {
                activeness.name = newName
                activenessRepo.saveActiveness(activeness)
                return true
            }
        }
        return false
    }

    fun isReadyToSwitch(activeness: Activeness): Boolean {
        val isReady = MainContext.activeActiveness == null

        if (isReady) {
            MainContext.activeActiveness = activeness
            MainContext.activeActivenessObservable = activenessRepo.getActivenessById(activeness.id)
        }

        return isReady
    }

    fun getFilterType() = MainContext.settings.filterType


}