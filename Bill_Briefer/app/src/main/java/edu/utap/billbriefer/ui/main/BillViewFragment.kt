package edu.utap.billbriefer.ui.main

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import edu.utap.billbriefer.MainActivity
import edu.utap.billbriefer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BillViewFragment : Fragment() {

    companion object {
        fun newInstance() = BillViewFragment()
        val constants = Constants()
        val TAG = "BillBriefer::BillViewFragment"
    }

    private lateinit var root: View
    private val viewModel:  MainViewModel by activityViewModels()
    private var bill: Bill? = null
    private var about_text: ConstraintLayout? = null
    private var about_title_arrow: ImageView? = null
    private var timeline_text: ConstraintLayout? = null
    private var timeline_title_arrow: ImageView? = null
    private var vote_text: ConstraintLayout? = null
    private var vote_title_arrow: ImageView? = null
    private var voters: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.bill_view_fragment, container, false)
        return root
    }

    fun fill_about() {

        about_text = root.findViewById<ConstraintLayout>(R.id.text_about)
        about_text!!.visibility = View.GONE
        about_title_arrow = root.findViewById<ImageView>(R.id.about_title_arrow)
        about_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_left_24)

        root.findViewById<ConstraintLayout>(R.id.title_about).setOnClickListener() {
            if(about_text!!.visibility == View.GONE) {
                about_text!!.visibility = View.VISIBLE
                about_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24)
            } else {
                about_text!!.visibility = View.GONE
                about_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_left_24)
            }
        }

        var organization_string = bill?.organization?.name ?: ""
        if(bill?.organization?.classification != null) {
            organization_string += ", " + bill!!.organization.classification
        }

        root.findViewById<TextView>(R.id.organization_tv).text = organization_string

        var classifications_string = ""
        if((bill?.classifications != null) && (bill?.classifications!!.size > 0)) {
            for(i in 0..bill!!.classifications.size-1) {
                if(classifications_string != "") {
                    classifications_string += "\n"
                }
                classifications_string += bill!!.classifications[i]
            }
        }

        var hyperlink_tv = root.findViewById<TextView>(R.id.hyperlink_tv)
        if((bill?.url != null) && (bill?.url  != "")) {
            hyperlink_tv.text = Html.fromHtml("<a href='" + bill!!.url + "'>Hyperlink</a>")
            hyperlink_tv.movementMethod = LinkMovementMethod.getInstance()
        } else {
            hyperlink_tv.text = ""
        }

        root.findViewById<TextView>(R.id.classifications_tv).text = classifications_string

        var subjects_string = ""

        if((bill?.subjects != null) && (bill!!.subjects.size > 0)) {
            for(i in 0..bill!!.subjects.size-1) {
                if(subjects_string != "") {
                    subjects_string += "\n"
                }
                subjects_string += bill!!.subjects[i]
             }
        }

        root.findViewById<TextView>(R.id.subjects_tv).text = subjects_string
    }

    fun fill_timeline() {

        timeline_text = root.findViewById<ConstraintLayout>(R.id.text_timeline)
        timeline_text!!.visibility = View.GONE
        timeline_title_arrow = root.findViewById<ImageView>(R.id.timeline_title_arrow)
        timeline_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_left_24)

        root.findViewById<ConstraintLayout>(R.id.title_timeline).setOnClickListener() {
            if(timeline_text!!.visibility == View.GONE) {
                timeline_text!!.visibility = View.VISIBLE
                timeline_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24)
            } else {
                timeline_text!!.visibility = View.GONE
                timeline_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_left_24)
            }
        }

        if(bill?.first_action_date != null) {
            root.findViewById<TextView>(R.id.first_action_tv).text = bill!!.first_action_date
        } else {
            root.findViewById<TextView>(R.id.first_action_tv).text = ""
        }

        if(bill?.latest_action_date != null) {
            root.findViewById<TextView>(R.id.latest_action_tv).text = bill!!.latest_action_date
        } else {
            root.findViewById<TextView>(R.id.latest_action_tv).text = ""
        }

        if(bill?.latest_action_description != null) {
            root.findViewById<TextView>(R.id.latest_action_desc_tv).text = bill!!.latest_action_description
        } else {
            root.findViewById<TextView>(R.id.latest_action_desc_tv).text = ""
        }
    }

    fun fill_vote() {
        vote_text = root.findViewById<ConstraintLayout>(R.id.text_vote)
        vote_text!!.visibility = View.GONE
        vote_title_arrow = root.findViewById<ImageView>(R.id.vote_title_arrow)
        vote_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_left_24)
        voters = root.findViewById<LinearLayout>(R.id.voters)

        if((bill?.votes_holder != null) && (bill!!.votes_holder.size > 0)) {
            root.findViewById<ConstraintLayout>(R.id.title_vote).setOnClickListener() {
                if (vote_text!!.visibility == View.GONE) {
                    vote_text!!.visibility = View.VISIBLE
                    vote_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24)
                } else {
                    vote_text!!.visibility = View.GONE
                    vote_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_left_24)
                }
            }

            if((bill!!.votes_holder[0].motion_classification != null) && (bill!!.votes_holder[0].motion_classification.size > 0)) {
                var classification_string = ""
                for(i in 0..bill!!.votes_holder[0].motion_classification.size-1) {
                    if(classification_string != "") {
                        classification_string += "\n"
                    }
                    classification_string += bill!!.votes_holder[0].motion_classification[i]
                }
                root.findViewById<TextView>(R.id.vote_motion_tv).text = classification_string
            } else {
                root.findViewById<TextView>(R.id.vote_motion_tv).text = ""
            }

            if(bill!!.votes_holder[0].date != null) {
                root.findViewById<TextView>(R.id.vote_date_tv).text = bill!!.votes_holder[0].date
            } else {
                root.findViewById<TextView>(R.id.vote_date_tv).text = ""
            }

            if(bill!!.votes_holder[0].result != null) {
                root.findViewById<TextView>(R.id.vote_result_tv).text = bill!!.votes_holder[0].result
            } else {
                root.findViewById<TextView>(R.id.vote_result_tv).text = ""
            }

            if((bill!!.votes_holder[0].votes != null) && (bill!!.votes_holder[0].votes.size > 0)) {
                val vi = getLayoutInflater()
                var has_person = false
                for(i in 0..bill!!.votes_holder[0].votes.size-1) {
                    if((bill!!.votes_holder[0].votes[i].result != null) && (bill!!.votes_holder[0].votes[i].voter?.name != null)) {

                        if(has_person) {
                            var spacer = TextView(requireContext())
                            spacer.textSize = 3.0.toFloat()
                            voters!!.addView(spacer)
                        } else {
                            has_person = true
                        }

                        var new_voter = vi.inflate(R.layout.voter_card, null)
                        var new_voter_tv = new_voter.findViewById<TextView>(R.id.vote)
                        new_voter_tv.text =  bill!!.votes_holder[0].votes[i].voter.name + ", " + bill!!.votes_holder[0].votes[i].result.toUpperCase()
                        if(bill!!.votes_holder[0].votes[i].result.toUpperCase() == "YES") {
                            new_voter_tv.setBackgroundColor(Color.parseColor(constants.GREEN))
                        } else if(bill!!.votes_holder[0].votes[i].result.toUpperCase() == "NO") {
                            new_voter_tv.setBackgroundColor(Color.parseColor(constants.RED))
                        }

                        new_voter.setOnClickListener() {

                            if((activity as MainActivity?)?.check_network() == true) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.searchPerson(bill!!.votes_holder[0].votes[i].voter.id)
                                }
                            }
                        }
                        voters!!.addView(new_voter)
                    }
                }
            }

        } else {
            root.findViewById<ConstraintLayout>(R.id.title_vote).visibility = View.GONE
        }


    }

    fun enable_person_viewer() {
        viewModel.observeViewPerson().observe(viewLifecycleOwner, Observer {
            if (it != null && it.lock != null && it.view_person != null && it.lock == false) {
                requireActivity().runOnUiThread {
                    requireActivity()?.supportFragmentManager?.beginTransaction()
                        .replace(R.id.container, PersonViewFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                }
            }
        })
    }

    fun set_back_press() {

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.lock_view_bill(true)
        }

        (activity as MainActivity?)?.beforeBackPress = {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.set_view_bill(null)
                viewModel.lock_view_bill(false)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity?)?.set_toolbar("Bill Information", true)
        Log.d(TAG, "Reached bill view fragment")

        bill = viewModel.get_view_bill()
        root.findViewById<TextView>(R.id.title).text = bill?.title

        set_back_press()
        fill_about()
        fill_timeline()
        fill_vote()
        enable_person_viewer()
    }
}