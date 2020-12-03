package edu.utap.billbriefer.ui.main

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.billbriefer.MainActivity
import edu.utap.billbriefer.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.math.max

class MainViewModel(application: Application, private val state: SavedStateHandle): AndroidViewModel(application) {
    // TODO: Implement the ViewModel
    companion object {
        val TAG = "BillBriefer::MainViewModel"
        val constants = Constants()
    }

    var apikey: String? = null
    private var api = OpenstateApi.create()
    private var repository = Repository(api)
    private var billList = MutableLiveData<List<Bill>>()
    private var page = MutableLiveData<Int>()
    private var max_page = MutableLiveData<Int>()
    private var num_results = MutableLiveData<Int>()
    private var view_bill = MutableLiveData<BillViewLocker?>()
    private var view_person = MutableLiveData<PersonViewLocker?>()
    private var jurisdiction: String? = null
    private var sort: String? = null

    suspend fun searchPerson(id: String): Int {
        var personSearch = repository.searchPerson(apikey ?: "", id)
        Log.d(TAG, "Found Person ${personSearch.toString()}")
        if(personSearch.return_code != null && personSearch.return_code.toInt() < 0 ) {
            return -1
        }

        set_view_person(personSearch)
        return 0
    }

    suspend fun searchBills(_jurisdiction: String, _sort: String): Int {
        jurisdiction = _jurisdiction
        sort = _sort
        var billSearch = repository.searchBills(apikey ?: "", jurisdiction!!, sort!!, 1.toString())
        Log.d(TAG, "got billserach")
        if(billSearch.return_code != null && billSearch.return_code.toInt() < 0 ) {
            return -1
        }

        Log.d(TAG, billSearch.toString())
        billList.postValue(billSearch.results)
        page.postValue(billSearch.pagination.page.toInt())
        max_page.postValue(billSearch.pagination.max_page.toInt())
        num_results.postValue(billSearch.pagination.total_items.toInt())

        Log.d(TAG, billSearch.toString())
        return 0
    }

    suspend fun getNextPage(): Int {
        var billSearch = repository.searchBills(apikey ?: "", jurisdiction!!, sort!!, ((page.value ?: 0) + 1).toString())
        Log.d(TAG, "got billserach")
        if(billSearch.return_code != null && billSearch.return_code.toInt() < 0 ) {
            return -1
        }

        billList.postValue(billSearch.results)
        page.postValue(billSearch.pagination.page.toInt())
        max_page.postValue(billSearch.pagination.max_page.toInt())
        num_results.postValue(billSearch.pagination.total_items.toInt())

        Log.d(TAG, billSearch.toString())
        return 0
    }

    suspend fun getPrevPage(): Int {
        var billSearch = repository.searchBills(apikey ?: "", jurisdiction!!, sort!!, ((page.value ?: 0) - 1).toString())
        Log.d(TAG, "got billserach")
        if(billSearch.return_code != null && billSearch.return_code.toInt() < 0 ) {
            return -1
        }

        billList.postValue(billSearch.results)
        page.postValue(billSearch.pagination.page.toInt())
        max_page.postValue(billSearch.pagination.max_page.toInt())
        num_results.postValue(billSearch.pagination.total_items.toInt())

        Log.d(TAG, billSearch.toString())
        return 0
    }

    suspend fun set_view_person(_view_person: PersonSearch?) {
        var start_view_person = view_person.value
        if(start_view_person == null) {
            start_view_person = PersonViewLocker(_view_person, false)
            view_person.postValue(start_view_person)
        } else {
            start_view_person.view_person = _view_person
            view_person.postValue(start_view_person)
        }
    }

    suspend fun set_view_bill(position: Int?) {
        var start_view_bill = view_bill.value
        if(start_view_bill == null) {
            start_view_bill = BillViewLocker(position, false)
            view_bill.postValue(start_view_bill)
        } else {
            start_view_bill.view_bill = position
            view_bill.postValue(start_view_bill)
        }
    }

    suspend fun lock_view_bill(lock: Boolean) {
        var start_view_bill = view_bill.value
        if(start_view_bill == null) {
            start_view_bill = BillViewLocker(0, lock)
            view_bill.postValue(start_view_bill)
        } else {
            start_view_bill.lock = lock
            view_bill.postValue(start_view_bill)
        }
    }

    suspend fun lock_view_person(lock: Boolean) {
        var start_view_person = view_person.value
        if(start_view_person == null) {
            start_view_person = PersonViewLocker(null, lock)
            view_person.postValue(start_view_person)
        } else {
            start_view_person.lock = lock
            view_person.postValue(start_view_person)
        }
    }

    fun get_view_person(): Person? {
        val localList = view_person.value?.view_person?.person
        localList?.let {
            return it[0]
        }
        return null
    }

    fun get_view_bill(): Bill? {
        val localList = billList.value?.toList()
        val position = view_bill.value?.view_bill ?: 0
        localList?.let {
            if( position >= it.size ) return null
            return it[position]
        }
        return null
    }

    var is_next_page = MediatorLiveData<Int>().apply {
        addSource(page) { value = isNextPage() }
        addSource(max_page) { value = isNextPage() }
        value = 0
    }

    private fun isNextPage(): Int {
        if(page.value ?: 0 < max_page.value ?: 0) {
            return 1
        } else {
            return 0
        }
    }

    fun observeViewBill(): LiveData<BillViewLocker?> {
        return view_bill
    }

    fun observeViewPerson(): LiveData<PersonViewLocker?> {
        return view_person
    }

    fun observeIsNextPage(): LiveData<Int> {
        return is_next_page
    }

    fun observePage(): LiveData<Int> {
        return page
    }

    fun observeMaxPage(): LiveData<Int> {
        return max_page
    }

    fun observeNumResults(): LiveData<Int> {
        return num_results
    }


    fun getBillAt(position: Int): Bill? {
        val localList = billList.value?.toList()
        localList?.let {
            if( position >= it.size ) return null
            return it[position]
        }
        return null
    }

    fun observeBillList(): LiveData<List<Bill>> {
        return billList
    }

    fun getBillCount(): Int {
        return billList?.value?.size ?: 0
    }
}