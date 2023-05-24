import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Pet
import com.mikuw.coupler.model.Search

class Datasource_Firebase_Searches {
    private val db = FirebaseFirestore.getInstance()

    fun loadSearches(callback: (List<Search>) -> Unit) {
        db.collection("searches")
            .get()
            .addOnSuccessListener { result ->
                val searches = mutableListOf<Search>()
                for (document in result) {
                    val title = document.getString("title") ?: continue
                    val location = document.getString("city") ?: continue
                    val from = document.getDate("from") ?: continue
                    val to = document.getDate("to") ?: continue
                    val creator = document.getString("creator") ?: continue
                    val desc = document.getString("desc") ?: continue
                    val pets = document.get("pets") as? List<Map<String, Any>> // Retrieve the pets array from Firestore
                    val petList = pets?.map { pet ->
                        val name = pet["name"] as? String ?: ""
                        val desc = pet["desc"] as? String ?: ""
                        val owner = pet["owner"] as? String ?: ""
                        val imageUri = pet["imageUri"] as? String ?: ""
                        // Create Pet objects based on the retrieved data
                        Pet(name, desc, owner, imageUri)
                    }
                    val search = Search(title, location, from, to, creator, desc, petList)
                    searches.add(search)
                }
                callback(searches)
            }
    }
}
