package com.example.ca3carrental

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragOthers : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view= inflater.inflate(R.layout.fragment_frag_others, container, false)
        val profile = view.findViewById<MaterialCardView>(R.id.cv1)
        val chalans = view.findViewById<MaterialCardView>(R.id.cv2)
        val contact = view.findViewById<MaterialCardView>(R.id.cv3)
        profile.setOnClickListener {
            val intent = Intent(requireContext(), Profile::class.java)
            startActivity(intent)
        }
        chalans.setOnClickListener {
          val intent=Intent(requireContext(),MainActivity2::class.java)
            startActivity(intent)
        }
        return view
    }

}