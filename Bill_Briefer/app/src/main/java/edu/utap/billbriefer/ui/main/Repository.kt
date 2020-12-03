package edu.utap.billbriefer.ui.main

class Repository(private val api: OpenstateApi) {
    // XXX Write me.
    suspend fun searchBills(apikey: String, jurisdiction: String, sort: String, page: String): BillSearch {
        return(api.getBills(apikey, jurisdiction, sort, page))
    }

    suspend fun searchPerson(apikey: String, id: String): PersonSearch {
        return(api.getPerson(apikey, id))
    }
}