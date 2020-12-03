package edu.utap.billbriefer.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.utap.billbriefer.MainActivity
import edu.utap.billbriefer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        private val TAG = "BillBriefer::MainFragment"
    }

    private val viewModel:  MainViewModel by activityViewModels()
    private lateinit var root: View
    private var state_s: Spinner? = null
    private var sort_s: Spinner? = null
    private var query_b: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.main_fragment, container, false)
        return root
    }

    private fun set_spinners() {
        state_s = root.findViewById<Spinner>(R.id.state_s)
        sort_s = root.findViewById<Spinner>(R.id.sort_s)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.state_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_layout)
            state_s!!.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_layout)
            sort_s!!.adapter = adapter
        }
    }

    private fun display_bill_search() {
        requireActivity().runOnUiThread {
            requireActivity()?.supportFragmentManager?.beginTransaction()
                .replace(R.id.container, BillSearchFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun set_query() {
        query_b = root.findViewById<Button>(R.id.query_b)
        query_b!!.setOnClickListener() {
            query_b!!.isClickable = false
            query_b!!.visibility = View.INVISIBLE
            Log.d(TAG, "Query button clicked")
            val jurisdiction = state_s!!.selectedItem.toString()
            val sort = sort_s!!.selectedItem.toString()

            CoroutineScope(Dispatchers.IO).launch {
                viewModel.searchBills(jurisdiction, sort)
                display_bill_search()
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity?)?.set_toolbar("Bill Briefer", false)
        set_spinners()
        set_query()
    }

}