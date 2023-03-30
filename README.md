# Client Side Android Application

This is an Android application that is part of the Two-Wheeler Doorstep Servicing Project. The application is inspired by the Urban-Clone project and includes features like booking system, place picker using Google Maps SDK, payment system using Razor Pay API and Firebase Cloud Functions, SMS notifications using MSG91 API, Firebase Cloud Messaging for push notifications, referrals using Firebase Dynamic Links, and SMS authentication using MSG91 API.

## Features

- **Booking System**: Allows customers to book a two-wheeler service through the app. 
- **Place Picker and Places Search Filter**: Customers can select their location using Google Maps SDK and search for places using Google Places API.
- **Payment System**: Customers can pay for their services through the app using Razor Pay API and Firebase Cloud Functions.
- **SMS**: The app sends SMS notifications to customers using the MSG91 API.
- **Notifications**: The app sends push notifications to customers using Firebase Cloud Messaging.
- **Referrals**: The app provides referral links using Firebase Dynamic Links.
- **SMS Authentication**: The app uses SMS authentication through MSG91 API for secure login.

## Technical Details

The app is built using Android Studio and written in Java. Google Maps SDK and Google Places API are integrated into the application to provide location selection for the customers. Firebase Real Time Database is used for the backend, which stores information about services, customers, and payments. RESTful APIs are used to interact with the database and retrieve data.

The app is designed using the Model-View-ViewModel (MVVM) architecture, which separates the user interface logic from the business logic. The UI components are designed using XML and the layout is managed by the ConstraintLayout.

The app uses Material Design components to provide a modern look and feel. The app also supports multiple languages with the use of localization.

## Getting Started

To run the app, you'll need to clone this repository and open the project in Android Studio. You'll also need to set up Google Maps and Places API keys, Razor Pay API keys, MSG91 API keys, and a Firebase account and add the respective configuration files to the app. Once you've set up everything, you can build and run the app on your Android device or emulator.

## Contributing

Contributions to this project are welcome. If you find a bug or have a feature request, please open an issue on the Github repository. If you would like to contribute code, please fork the repository and create a pull request.

## Developer
Jay Birthariya 
