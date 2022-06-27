# Eats

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
1. [Schema](#Schema)

## Overview
### Description
 App allows users to share food they made with others. Users can sell food or give it for free. Users can also find food near them, including resturants.

### App Evaluation
- **Category:** Social Media/ Food
- **Mobile:** The app is intended for use in mobile devices. It will be primary be built for andriod but can be extended to iOS. It can also have a web presence to allow for flexibility and access on computers.
- **Story:** Helps users share food with others. Users can sell food they made or give it out for free
- **Market:** The target market is people looking for a place to eat or try homecooked food by someone else
- **Habit:** The app can be used whenever the user is in search of something to eat
- **Scope:** The initial focus is helping people find homecooked food near them. It can be extended to enable people acquire the required documentation and certification to trade food.

## Product Spec
### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Sign In page to enable users to login
* Sign Up page where users can create an account
* MapView where users can see food places near them
* Detail view where users can get more detail about the food or resturant

* user profile view: user can see their profile and that of others

**Optional Nice-to-have Stories**

* Enable users to comment on a post
* Enable users to share a post
* * Search/filter: users can search for specfic items or places

### 2. Screen Archetypes

** Login 
* Register - User signs up or logs into their account
   * Upon Download/Reopening of the application, the user is prompted to log in to gain access to their profile or to sign up for a new account. 
   * ...
* Map
   * user can see homes selling or sharing food and resturants near them and anywhere in the world
* Profile Screen 
   * User sees their previous posts. Visiting someone else' profile enables the user to follow or block that user and also to see posts that were made by that user
* Search.
   * Search bar on top allows user to search for particular food or places.
* Details Screen
   * Allows people to get more details about the food they are interested in and the place it is being sold

### 3. Navigation


***Tab Navigation** (Tab to Screen)

* Map
* Profile
* Search

**Flow Navigation** (Screen to Screen)
* Forced Log-in -> Account creation if no log in is available
* MapView -> clicking on an item goes to detail view of that item
* Profile -> list of previous post made by that user. clicking an item gives option to delete or go to details view 
* Search -> text field to enter query and button to trigger search

## Wireframes
![](EatsScreens.png)

### [BONUS] Digital Wireframes & Mockups


### [BONUS] Interactive Prototype

## Schema 
### Models
#### Post

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | user        | Pointer to User| image author |
   | image         | File     | image that user posts |
   | caption       | String   | image caption by author |
   | commentsCount | Number   | number of comments that has been posted to an image |
   | likesCount    | Number   | number of likes for the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   | location     | Array of Strings| Contains the latitude of the user location at index 0 and longitude at index 1|
   | price     | Number| The cost of the food per item|
   | comments     | Array of Strings| All commments on a post|
   
   #### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | userName      | String   | the screen name of the user |
   | userProfilePic| File     | image to be used as avatar for user |
   | bio           | String(Optional)| Offers a description about the user |
   | password | Alphanumeric   | password user uses for authentication |
   | email    | String   | email account associated with user to be used for two-step verifcaition and notifications from app |
   | createdAt     | DateTime | date when user created account (default field) |
   | updatedAt     | DateTime | date when user last updated their account (default field) |
   | followers    | Array of Strings (or pointers if possible)| Contains pointers to all users following user or their userIds|
   | following    | Array of Strings (or pointers if possible)| Contains pointers to all users that the user follows or their userIds|
   | bank details     | Object (Optional--Stretch Feature)| Bank details- card number, cvv, name on account, expiry month and year- to enable transactions|
   | pastTranscations     | Object(Stretch Feature)| Contains all of the user's past transactions, both purchases and sales|
### Networking
#### List of network requests by screen
   - Home Feed Screen
      - (Read/GET) Query all posts of food currently in database 
         ```Java
        queryPosts() {
            ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
            query.setLimit(20); //load first 20 posts
            query.include(Post.USER);
            query.addDescendingOrder("createdAt");
            query.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> posts, ParseException e) {
                    if(e != null) {
                        Log.i("HOME", "something went wrong obtaining posts " + e);
                    }

                    // save received posts to list and notify adapter of new data
                    mAllPosts.addAll(posts);
                    mPostsAdapter.notifyDataSetChanged();
                   mScrollListener.resetState();
                }
            });
        }
         ```
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
   - Create Post Screen
      - (Create/POST) Create a new post object
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile image
      - (Delete) Delete a post
    - Map Screen
      - (Read/GET) Query the user's current location
      - (Read/GET) Query all posts of food currently in database and return their location
#### [OPTIONAL:] Existing API Endpoints
##### An API Of Ice And Fire
