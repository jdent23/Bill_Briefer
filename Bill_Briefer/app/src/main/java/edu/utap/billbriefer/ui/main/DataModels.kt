package edu.utap.billbriefer.ui.main

import com.google.gson.annotations.SerializedName
import edu.utap.billbriefer.R

class Constants {
    val REQUEST_ERROR = -1
    val RED: String = "#FFCCCC"
    val BLUE: String = "#CCCCFF"
    val GREEN: String = "#CCFFCC"
}

data class Voter (
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("party")
    val party: String
)

data class Votes (
    @SerializedName("option")
    val result: String,
    @SerializedName("voter")
    val voter: Voter
)

data class VotesHolder (
    @SerializedName("result")
    val result: String,
    @SerializedName("start_date")
    val date: String,
    @SerializedName("motion_classification")
    val motion_classification: List<String>,
    @SerializedName("votes")
    val votes: List<Votes>
)

data class Organization (
        @SerializedName("name")
        val name: String,
        @SerializedName("classification")
        val classification: String,
)

data class Bill (
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("classification")
    val classifications: List<String>,
    @SerializedName("openstates_url")
    val url: String,
    @SerializedName("subject")
    val subjects: List<String>,
    @SerializedName("votes")
    val votes_holder: List<VotesHolder>,
    @SerializedName("from_organization")
    val organization: Organization,
    @SerializedName("first_action_date")
    val first_action_date: String,
    @SerializedName("latest_action_date")
    val latest_action_date: String,
    @SerializedName("latest_action_description")
    val latest_action_description: String
)

data class Pagination(
    @SerializedName("total_items")
    val total_items: String,
    @SerializedName("page")
    val page: String,
    @SerializedName("max_page")
    val max_page: String
)

data class BillSearch(
    @SerializedName("results")
    val results: List<Bill>,
    @SerializedName("return_code")
    val return_code: String,
    @SerializedName("pagination")
    val pagination: Pagination
)




data class Jurisdiction(
    @SerializedName("name")
    val name: String,
    @SerializedName("classification")
    val classification: String
)

data class Role(
    @SerializedName("title")
    val title: String,
    @SerializedName("org_classification")
    val org_classification: String,
    @SerializedName("district")
    val district: String,
)

data class Person(
    @SerializedName("name")
    val name: String,
    @SerializedName("party")
    val party: String,
    @SerializedName("current_role")
    val role: Role,
    @SerializedName("jurisdiction")
    val jurisdiction: Jurisdiction,
    @SerializedName("image")
    val image_url: String,
    @SerializedName("birth_date")
    val birth_date: String,
    @SerializedName("openstates_url")
    val url: String,
    @SerializedName("email")
    val email: String
)


data class PersonSearch(
    @SerializedName("results")
    val person: List<Person>,
    @SerializedName("return_code")
    val return_code: String
)

data class PersonViewLocker(
    var view_person: PersonSearch?,
    var lock: Boolean?
)

data class BillViewLocker(
    var view_bill: Int?,
    var lock: Boolean?
)