# Refactoring FriendlyChat

This repository contains the refactoring code for the FriendlyChat project in the [Firebase in a Weekend: Android by Google](https://www.udacity.com/course/firebase-in-a-weekend-by-google-android--ud0352) Udacity course.

## Overview

FriendlyChat is an app that allows users to send and receive text and photos in realtime across platforms. When you finish the course linked above you try to figure out how you could use firebase features into a real app and here comes the problems. Infact, FriendlyChat app at the finish of the course is a complete mess. All features are into the MainActivity class. Where is the best practice here?

In this repository i try to give a possible solution for this problem. I have followed the best practices from *Clean Code* book and for refactoring i have followed *Refactorings* by Martin Fowler.

Here the big refactorings done:
- Firebase features extract from MainActivity to Repository.
- UI elements extract from MainActivity to ChatFragment. https://github.com/EduBic/FirebaseToyApp/wiki/Extract-Fragment-from-Activity
- Implement MVP.

## Setup

Setup requires creating a Firebase project. See https://firebase.google.com/ for more information.

## License
See [LICENSE](LICENSE)
