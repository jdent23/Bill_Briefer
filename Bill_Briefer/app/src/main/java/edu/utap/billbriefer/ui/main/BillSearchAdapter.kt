package edu.utap.billbriefer.ui.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import edu.utap.billbriefer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class BillSearchAdapter(private val viewModel: MainViewModel)
    : RecyclerView.Adapter<BillSearchAdapter.VH>() {

    companion object {
        val constants = Constants()
        val TAG = "BillBriefer::BillSearchAdapter"
    }


    inner class VH(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val subjects = itemView.findViewById<TextView>(R.id.subjects)
        val status = itemView.findViewById<TextView>(R.id.status)

        init {
            itemView.setOnClickListener() {
                var view_bill_position = this.adapterPosition
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(TAG, "Trying to set view bill")
                    viewModel.set_view_bill(view_bill_position)
                }
            }
        }

        fun bind(item: Bill?, position: Int) {
            item?.let {
                title.text = item.title
                var subjects_string = ""
                if(item.subjects != null) {
                    for(i in 0..item.subjects.size-1) {
                        if(subjects_string != "") {
                            subjects_string += "\n"
                        }
                        subjects_string += item.subjects[i]
                    }
                }
                subjects.text = subjects_string

                if(subjects.text == "") {
                    subjects.visibility = View.GONE
                } else {
                    subjects.visibility = View.VISIBLE
                }

                if((item.votes_holder == null) || (item.votes_holder.size == 0) || (item.votes_holder[0].votes == null)) {
                    if(item.latest_action_description == null || item.latest_action_description == "") {
                        status.text = "UNKNOWN"
                    } else {
                        status.text = item.latest_action_description.toUpperCase()
                    }
                    status.setBackgroundColor(Color.parseColor(constants.BLUE))
                } else if(item.votes_holder[0].result == "pass") {
                    status.text = "PASS"
                    status.setBackgroundColor(Color.parseColor(constants.GREEN))
                } else {
                    status.text = item.votes_holder[0].result.toUpperCase()
                    status.setBackgroundColor(Color.parseColor(constants.RED))
                }

                if(status.text == "") {
                    status.visibility = View.GONE
                } else {
                    status.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.bill_card, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(viewModel.getBillAt(holder.adapterPosition), position)
    }

    override fun getItemCount() = viewModel.getBillCount()
}