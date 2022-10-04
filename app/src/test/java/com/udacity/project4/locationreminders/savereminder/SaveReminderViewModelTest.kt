package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.rule.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(maxSdk = Build.VERSION_CODES.P)
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var fakeDataSource: FakeDataSource

    // Executes each task synchronously using Architecture Components.

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        stopKoin()
        // Initialise the fake Data Source with no reminders.
        fakeDataSource = FakeDataSource()
        // Initialize the saveReminderViewModel
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    private fun getReminder() = ReminderDataItem(
        title = "title",
        description = "description",
        location = "location",
        latitude = 23.80,
        longitude = 45.36
    )

  //testing for save reminder item
    @Test
    fun saveReminder() = mainCoroutineRule.runBlockingTest {
        //GIVEN: user enter a place to remind it.
        val reminder = getReminder()
        //WHEN: user click to save it.
        saveReminderViewModel.saveReminder(reminder)
        //THEN: the user see toast that place is saved.
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
    }
    //testing for save reminder item when Title Empty
    @Test
    fun saveReminder_When_TitleEmpty() = mainCoroutineRule.runBlockingTest {
        //GIVEN: user enter a place to remind it without title.
        val reminder = getReminder()
        reminder.title = ""
        //WHEN: user click to save it.
        saveReminderViewModel.validateAndSaveReminder(reminder)
        //THEN: place will not save not valid.
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())
    }
    //testing for save reminder item when location Empty
    @Test
    fun saveReminder_When_LocationEmpty() = mainCoroutineRule.runBlockingTest {
        // GIVEN a reminder item without location
        val reminder = getReminder()
        reminder.location = ""
        // WHEN validate reminder data
        saveReminderViewModel.validateAndSaveReminder(reminder)
        //Assert validation when no location return false
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())
    }
    //testing for save reminder item when Title and location Empty
    @Test
    fun saveReminder_When_LocationEmpty_TitleEmpty() = mainCoroutineRule.runBlockingTest {
        // GIVEN a reminder item without title and location
        val reminder = getReminder()
        reminder.title = ""
        reminder.location = ""

        // WHEN validate reminder data
        saveReminderViewModel.validateAndSaveReminder(reminder)
        //Assert validation when no title and no location return false
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())
    }
//stop koin
    @After
    fun stopDown() {
        stopKoin()
    }
}