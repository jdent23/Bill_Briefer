package edu.utap.billbriefer.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.billbriefer.MainActivity
import edu.utap.billbriefer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BillSearchFragment : Fragment() {

    companion object {
        fun newInstance() = BillSearchFragment()
        val TAG = "BillBriefer::BillSearchFragment"
    }

    private lateinit var root: View
    private val viewModel:  MainViewModel by activityViewModels()
    private var rv: RecyclerView? = null
    private var adapter: BillSearchAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.recycle_view, container, false)
        return root
    }

    private fun set_rv() {
        rv = root.findViewById<RecyclerView>(R.id.rv)
        rv?.layoutManager = LinearLayoutManager(context)
        Log.d(TAG, "Using rv: ${rv.toString()}")
        adapter = BillSearchAdapter(viewModel)
        rv?.adapter = adapter


        viewModel.observeBillList().observe(viewLifecycleOwner, Observer {
            adapter!!.notifyDataSetChanged()
        })
    }

    fun set_page_buttons() {
        val next_page_b = root.findViewById<Button>(R.id.next_page_b)
        val prev_page_b = root.findViewById<Button>(R.id.prev_page_b)
        next_page_b.isClickable = false
        prev_page_b.isClickable = false

        next_page_b.setOnClickListener() {
            next_page_b.isClickable = false
            next_page_b.visibility = View.INVISIBLE
            rv!!.scrollToPosition(0)

            if((activity as MainActivity?)?.check_network() == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getNextPage()
                }
            } else {
                next_page_b.isClickable = true
                next_page_b.visibility = View.VISIBLE
            }

        }

        prev_page_b.setOnClickListener() {
            prev_page_b.isClickable = false
            prev_page_b.visibility = View.INVISIBLE
            rv!!.scrollToPosition(0)

            if((activity as MainActivity?)?.check_network() == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getNextPage()
                }
            } else {
                prev_page_b.isClickable = true
                prev_page_b.visibility = View.VISIBLE
            }

        }

        viewModel.observePage().observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "Value of it ${it.toString()}")
            if(it > 1) {
                prev_page_b.isClickable = true
                prev_page_b.visibility = View.VISIBLE
            } else {
                prev_page_b.isClickable = false
                prev_page_b.visibility = View.INVISIBLE
            }
        })

        viewModel.observeIsNextPage().observe(viewLifecycleOwner, Observer {
            if(it > 0) {
                next_page_b.isClickable = true
                next_page_b.visibility = View.VISIBLE
            } else {
                next_page_b.isClickable = false
                next_page_b.visibility = View.INVISIBLE
            }
        })
    }

    fun set_results() {
        val num_results_tv = root.findViewById<TextView>(R.id.num_results_tv)
        val max_page_tv = root.findViewById<TextView>(R.id.max_page_tv)
        val page_tv = root.findViewById<TextView>(R.id.page_tv)

        viewModel.observeNumResults().observe(viewLifecycleOwner, Observer {
            num_results_tv.text = it.toString()
        })

        viewModel.observePage().observe(viewLifecycleOwner, Observer {
            page_tv.text = it.toString()
        })

        viewModel.observeMaxPage().observe(viewLifecycleOwner, Observer {
            max_page_tv.text = it.toString()
        })
    }

    fun set_bill_viewer() {
        viewModel.observeViewBill().observe(viewLifecycleOwner, Observer {
            if(it != null && it.lock != null && it.view_bill != null && it.lock != true) {
                display_bill()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity?)?.set_toolbar("Results", true)
        set_rv()
        set_page_buttons()
        set_results()
        set_bill_viewer()
    }

    fun display_bill() {
        requireActivity().runOnUiThread {
            requireActivity()?.supportFragmentManager?.beginTransaction()
                .replace(R.id.container, BillViewFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }
}