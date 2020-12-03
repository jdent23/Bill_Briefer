package edu.utap.billbriefer.ui.main

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
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
import edu.utap.billbriefer.ui.main.Glide.glideFetch


class PersonViewFragment : Fragment() {

    companion object {
        fun newInstance() = PersonViewFragment()
        val constants = Constants()
        val TAG = "BillBriefer::PersonViewFragment"
    }

    private lateinit var root: View
    private val viewModel:  MainViewModel by activityViewModels()
    private var personal_text: ConstraintLayout? = null
    private var personal_title_arrow: ImageView? = null
    private var representative_text: ConstraintLayout? = null
    private var representative_title_arrow: ImageView? = null
    private var view_person: Person? = null
    private var profile_iv: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.person_view_fragment, container, false)
        return root
    }

    fun set_on_back_press() {

        CoroutineScope(Dispatchers.IO).launch() {
            viewModel.lock_view_person(true)
        }

        (activity as MainActivity)?.beforeBackPress = {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.set_view_person(null)
                viewModel.lock_view_person(false)
            }
        }
    }

    fun set_profile_iv() {
        if(view_person!!.image_url != null) {
            profile_iv = root.findViewById<ImageView>(R.id.profile_iv)
            glideFetch(view_person!!.image_url, profile_iv!!)
        }

        if(view_person!!.name != null) {
            root.findViewById<TextView>(R.id.title).text = view_person!!.name
        }
    }

    fun set_personal() {


        personal_text = root.findViewById<ConstraintLayout>(R.id.text_personal)
        personal_text!!.visibility = View.GONE
        personal_title_arrow = root.findViewById<ImageView>(R.id.personal_title_arrow)
        personal_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_left_24)

        root.findViewById<ConstraintLayout>(R.id.title_personal).setOnClickListener() {
            if(personal_text!!.visibility == View.GONE) {
                personal_text!!.visibility = View.VISIBLE
                personal_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24)
            } else {
                personal_text!!.visibility = View.GONE
                personal_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_left_24)
            }
        }

        if(view_person!!.birth_date != null) {
            root.findViewById<TextView>(R.id.birthday_tv).text = view_person!!.birth_date
        } else {
            root.findViewById<TextView>(R.id.birthday_tv).text = ""
        }

        if(view_person!!.party != null) {
            root.findViewById<TextView>(R.id.party_tv).text = view_person!!.party
        } else {
            root.findViewById<TextView>(R.id.party_tv).text = ""
        }

        if(view_person!!.email != null) {
            root.findViewById<TextView>(R.id.email_tv).text = view_person!!.email
        } else {
            root.findViewById<TextView>(R.id.email_tv).text = ""
        }

        var hyperlink_tv = root.findViewById<TextView>(R.id.hyperlink_tv)
        if(view_person!!.url != null) {
            hyperlink_tv.text = Html.fromHtml("<a href='" + view_person!!.url + "'>Hyperlink</a>")
            hyperlink_tv.movementMethod = LinkMovementMethod.getInstance()
        } else {
            hyperlink_tv.text = ""
        }
    }

    fun set_representative() {

        representative_text = root.findViewById<ConstraintLayout>(R.id.text_representative)
        representative_text!!.visibility = View.GONE
        representative_title_arrow = root.findViewById<ImageView>(R.id.representative_title_arrow)
        representative_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_left_24)

        root.findViewById<ConstraintLayout>(R.id.title_representative).setOnClickListener() {
            if(representative_text!!.visibility == View.GONE) {
                representative_text!!.visibility = View.VISIBLE
                representative_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24)
            } else {
                representative_text!!.visibility = View.GONE
                representative_title_arrow!!.setBackgroundResource(R.drawable.ic_baseline_arrow_left_24)
            }
        }

        var role_string = ""
        var district_string = ""
        if(view_person!!.role != null) {
            if (view_person!!.role.title != null && view_person!!.role.title != "") {
                role_string += view_person!!.role.title
                if (view_person!!.role.org_classification != null && view_person!!.role.org_classification != "") {
                    role_string += ", " + view_person!!.role.org_classification
                }
            }

            if(view_person!!.role.district != null) {
                district_string += view_person!!.role.district
            }
        }
        root.findViewById<TextView>(R.id.role_tv).text = role_string
        root.findViewById<TextView>(R.id.district_tv).text = district_string

        var jurisdiction_string = ""
        if(view_person!!.jurisdiction != null && view_person!!.jurisdiction.name != null && view_person!!.jurisdiction.name != "") {
            jurisdiction_string += view_person!!.jurisdiction.name
            if(view_person!!.jurisdiction.classification != null) {
                jurisdiction_string += ", " + view_person!!.jurisdiction.classification
            }
        }
        root.findViewById<TextView>(R.id.jurisdiction_tv).text = jurisdiction_string
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity?)?.set_toolbar("Person Information", true)
        Log.d(TAG, "Reached person view fragment")

        set_on_back_press()
        view_person = viewModel.get_view_person()

        if(view_person != null) {
            set_profile_iv()
            set_personal()
            set_representative()
        }
        Log.d(TAG, view_person.toString())

    }
}