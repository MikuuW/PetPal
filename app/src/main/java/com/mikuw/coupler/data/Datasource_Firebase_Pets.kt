import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Pet

class Datasource_Firebase_Pets {
    private val db = FirebaseFirestore.getInstance()

    fun loadPets(callback: (List<Pet>) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        db.collection("pets")
            .whereEqualTo("owner", currentUser?.uid)
            .get()
            .addOnSuccessListener { result ->
                val pets = mutableListOf<Pet>()
                for (document in result) {
                    val name = document.getString("name") ?: continue
                    val desc = document.getString("desc") ?: continue
                    val owner = document.getString("owner") ?: continue
                    val type = document.getString("type") ?: continue
                    val pet = Pet(name, desc, owner, type)
                    pets.add(pet)
                }
                callback(pets)
            }
    }
}
