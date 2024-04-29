package com.example.ca3carrental

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Locale
import java.util.UUID

class AddCars : AppCompatActivity() {
    private lateinit var carNameEditText: EditText
    private lateinit var manufacturerEditText: EditText
    private lateinit var seatsEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var mileageEditText: EditText
    private lateinit var modelEditText: EditText
    private lateinit var yearEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var fuelTypeRadioGroup: RadioGroup
    private lateinit var imageBitmap: Bitmap
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val REQUEST_IMAGE_CAPTURE = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cars)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Initialize UI elements
        carNameEditText = findViewById(R.id.edit_text_car_name)
        manufacturerEditText = findViewById(R.id.edit_text_manufacturer)
        seatsEditText = findViewById(R.id.edit_text_seats)
        priceEditText = findViewById(R.id.edit_text_price)
        mileageEditText = findViewById(R.id.edit_text_mileage)
        modelEditText = findViewById(R.id.edit_text_model)
        yearEditText = findViewById(R.id.edit_text_year)
        fuelTypeRadioGroup = findViewById(R.id.radio_group_fuel_type)
        locationEditText=findViewById(R.id.locationTextView)
        val captureImage = findViewById<Button>(R.id.btn_upload_image)
        locationEditText.setOnClickListener {
            fetchCurrentLocation()
        }

        // Set click listener for capturing image
        captureImage.setOnClickListener {
            val options = arrayOf("Capture Image", "Pick from Gallery")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose Image Source")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> captureImage()
                    1 -> pickImageFromGallery()
                }
            }
            builder.show()
        }

        // Set click listener for adding car
        val addCarButton = findViewById<Button>(R.id.button_add_car)
        addCarButton.setOnClickListener {
            // Upload image to Firebase Storage and save data to Realtime Database
            uploadImageToFirebaseStorage(imageBitmap)
        }
    }

    private fun captureImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
//            val imageData = data.extras?.get("data")
//            if (imageData is Bitmap) {
//                imageBitmap = imageData
//                Toast.makeText(this, "Image Captured", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
        if (data != null) {
            val imageData: Any? = data.extras?.get("data")
            if (imageData is Bitmap) {
                // Image captured by the camera
                imageBitmap = imageData
                Toast.makeText(this, "Image Captured", Toast.LENGTH_SHORT).show()
            } else if (data.data != null) {
                // Image picked from the gallery
                val selectedImageUri: Uri? = data.data
                selectedImageUri?.let {
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        Toast.makeText(this, "Image Picked", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Failed to pick image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}


    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap) {
        // Generate a random UUID to use as the filename
        val fileName = UUID.randomUUID().toString()

        // Reference to the Firebase Storage location where you want to upload the image
        val storageRef = FirebaseStorage.getInstance().getReference("/images/$fileName.jpg")

        // Convert the bitmap to a byte array
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Upload the image to Firebase Storage
        storageRef.putBytes(data)
            .addOnSuccessListener { uploadTask ->
                // Image upload success
                Log.d("Upload", "Image uploaded successfully")

                // Get the download URL of the uploaded image
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    // Call a function to save the data along with the image URL to the Realtime Database
                    saveDataToRealtimeDatabase(imageUrl)
                }.addOnFailureListener {
                    // Failed to get the download URL
                    Log.e("Upload", "Failed to get download URL: ${it.message}")
                }
            }
            .addOnFailureListener {
                // Image upload failed
                Log.e("Upload", "Failed to upload image: ${it.message}")
            }
    }

    private fun saveDataToRealtimeDatabase(imageUrl: String) {
        // Get other car details from UI
        val carName = carNameEditText.text.toString()
        val manufacturer = manufacturerEditText.text.toString()
        val seats = seatsEditText.text.toString().toInt()
        val price = priceEditText.text.toString().toDouble()
        val mileage = mileageEditText.text.toString()
        val model = modelEditText.text.toString()
        val year = yearEditText.text.toString().toInt()
        val fuelType = findViewById<RadioButton>(fuelTypeRadioGroup.checkedRadioButtonId).text.toString()
        val location=locationEditText.text.toString()

        // Save the data along with the image URL to the Realtime Database
        val database = FirebaseDatabase.getInstance().reference.child("cars")
        val carId = generateCarId()
        val car = Car(
            carId,
            carName,
            manufacturer,
            seats,
            price,
            mileage,
            model,
            year,
            fuelType,
            imageUrl,
            location // Set the image URL
        )
        database.child(carId).setValue(car)
            .addOnSuccessListener {
                Toast.makeText(this, "Car added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add car", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateCarId(): String {
        return "CAR-${UUID.randomUUID().toString().substring(0, 8)}"
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun fetchCurrentLocation() {
        // Check location permissions
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            // Permission has already been granted
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                        // Assign location coordinates to the locationEditText field
                        locationEditText.setText(list[0].getAddressLine(0).toString())
                    } else {
                        // Location is null
                        Toast.makeText(
                            this,
                            "Unable to retrieve current location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Toast.makeText(
                        this,
                        "Failed to retrieve current location: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, fetch the current location
                    fetchCurrentLocation()
                } else {
                    // Permission denied
                    Toast.makeText(
                        this,
                        "Permission denied. Unable to retrieve current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }
    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100
    }
}
