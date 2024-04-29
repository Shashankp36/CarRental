package com.example.ca3carrental
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragHome : Fragment() {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var carList: MutableList<Car>
    private lateinit var carAdapter: CarAdapterHome


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_frag_home, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        carList = mutableListOf()
        carAdapter = CarAdapterHome(requireContext(), carList)
        recyclerView.adapter = carAdapter
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val images = listOf(
            R.drawable.sedan,
            R.drawable.sedan,
            R.drawable.sedan
        )
        val adapter1 = ImageAdapter(images)
        viewPager.adapter = adapter1
        viewPager.setPageTransformer(HorizontalPeekPageTransformer())

        databaseRef = FirebaseDatabase.getInstance().getReference("cars")
        databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        carList.clear()
                        for (carSnapshot in snapshot.children) {
                            val car = carSnapshot.getValue(Car::class.java)
                            car?.let { carList.add(it) }
                        }
                        carAdapter.notifyDataSetChanged()
                    }
                }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })

        return view
    }
}