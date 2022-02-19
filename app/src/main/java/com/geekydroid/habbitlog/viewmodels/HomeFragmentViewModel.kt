package com.geekydroid.habbitlog.viewmodels

import androidx.lifecycle.*
import com.geekydroid.habbitlog.entities.Habit
import com.geekydroid.habbitlog.repo.HomeFragmentRepository
import kotlinx.coroutines.launch

class HomeFragmentViewModel(private val repo: HomeFragmentRepository) : ViewModel() {




    val searchText = MutableLiveData("")

    val habits = getAllHabits()


    private fun getAllHabits(): LiveData<List<Habit>> {
        return Transformations.switchMap(searchText) {
            repo.getAllHabits(it)
        }
    }


}


class HomeFragmentViewModelFactory(private val repo: HomeFragmentRepository) :
    ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     *
     * @param modelClass a `Class` whose instance is requested
     * @return a newly created ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(HomeFragmentRepository::class.java).newInstance(repo)
    }

}