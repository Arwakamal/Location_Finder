package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.rule.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest : AutoCloseKoinTest() {

    // Use a fake data source to be injected into the view model.
    private lateinit var fakeReminderDataSource: FakeDataSource
    private lateinit var remindersViewModel: RemindersListViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    //Initialize fakeReminderDataSource & remindersViewModel
    @Before
    fun setupViewModel() {
        // Initialise the fakeReminderDataSource with no reminders.
        fakeReminderDataSource = FakeDataSource()
       //Initialise the remindersViewModel
        remindersViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeReminderDataSource)
    }
    //testing for error in fake data
    @Test
    fun testShouldReturnError () = runBlockingTest  {
        //error in fake data
        fakeReminderDataSource.setShouldReturnError(true)
        saveReminderFakeData()
        //try to load data
        remindersViewModel.loadReminders()
        //error will appear
        MatcherAssert.assertThat(
            remindersViewModel.showSnackBar.value, CoreMatchers.`is`("Reminders not found")
        )
    }
   // testing for check loading
    @Test
    fun check_loading() = runBlockingTest {

        mainCoroutineRule.pauseDispatcher()
        saveReminderFakeData()
       // WHEN load reminders from data source
        remindersViewModel.loadReminders()

        MatcherAssert.assertThat(remindersViewModel.showLoading.value, CoreMatchers.`is`(true))

        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(remindersViewModel.showLoading.value, CoreMatchers.`is`(false))
    }
    //save Reminder FakeData
    private suspend fun saveReminderFakeData() {
        fakeReminderDataSource.saveReminder(
            ReminderDTO(
                "title abc",
                "description abc",
                "location abc",
                77.00,
                77.00)
        )
    }
}