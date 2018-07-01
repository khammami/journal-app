# Journal App

As the name of the project state, Journal App is a journal application where the users can pen down their thoughts and feelings.
it's a project proposed by ALC for the #7DaysofCodeChallenge

The app take advantage of Architecture Components and other components like: Firebase for authentication with Google and Firestore as remote database for persistence, Room library to manage local persistence and offline use.

## Getting Started

You may clone or [download](https://github.com/khammami/journal-app/archive/master.zip) the repo to your local machine and build your own app with Android Studio or just install the demo apk within the repo.

For Google Authentication & Firebase you need to use your own google-services.json. Here how to get yours:

* [Authenticate Using Google Sign-In on Android](https://firebase.google.com/docs/auth/android/google-signin)
* [Adding the JSON File](https://developers.google.com/android/guides/google-services-plugin#adding_the_json_file)

You may need to include an api key in your [manifest.xml](https://github.com/khammami/journal-app/blob/411291d3934ded74f1d1c4bfec80a900a321c2a8/app/src/main/AndroidManifest.xml#L29) for Fabric if you want or just remove it.

**Firestore rules I've used:**
```
match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth.uid == userId;
    }
    
    match /users/{userId}/posts/{postId} {
      allow write: if request.resource.data.updatedAt > resource.data.updatedAt;
      allow write: if !exists(/databases/$(database)/documents/users/$(request.auth.uid)/posts/$(request.resource.data.id))
    }
  }
  ```

### Screenshots
<img src="https://raw.githubusercontent.com/khammami/journal-app/master/release/screenshots/Screenshot_2018-07-01-16-23-44.png" width="150"> <img src="https://raw.githubusercontent.com/khammami/journal-app/master/release/screenshots/Screenshot_2018-07-01-16-23-33.png" width="150">

### Prerequisites

* Android Studio 3
* ADB
* JDK 1.7

### Installing

To install the demo just go to Menu > Settings > Security > and check Unknown Sources to allow your phone to install apps 
from sources other than the Google Play Store and download the demo:

[download the demo](https://github.com/khammami/journal-app/raw/master/release/app-release.apk)

## Built With

* [Picasso](http://square.github.io/picasso/)
* [Maven](https://maven.apache.org/)
* [Color picker](https://android.googlesource.com/platform/frameworks/opt/colorpicker/)

## Authors

* **Khalil Hammami** - *Initial work* - [khammami](https://github.com/khammami)

See also the list of [contributors](https://github.com/khammami/journal-app/graphs/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* AlcWithGoogle
* Andela community
* Udacity
