# ARcore-measurement

forked form the [legend](https://medium.com/@shibuiyusuke/measuring-distance-with-arcore-6eb15bf38a8f)

## Faced Errors

### 1. java runtime jdr

the solution from [here](https://stackoverflow.com/questions/66449161/how-to-upgrade-an-android-project-to-java-11)

### 2. [NDK](https://www.youtube.com/watch?v=tPiDwW3a43k), 9min

---

## New stuff with Android Studio

1. [Spinner](https://www.youtube.com/watch?v=ovGZYK9bq2o), 12min
2. [layouts and layoutParams](https://www.youtube.com/watch?v=bKBQG8xadZE), 4min
3. [linearLayout](https://www.youtube.com/watch?v=yx3xzuNCJKc), 16min
4. [tableLayout](https://www.youtube.com/watch?v=2q7R3Pt-NCw), 10min
5. [Fragments](https://www.youtube.com/watch?v=-vAI7RSPxOA), 17min

### 1. [Spinner and Array Adapter](https://www.geeksforgeeks.org/spinner-in-android-using-java-with-example/)

Spinner is a view similar to the dropdown list which is used to select one option from the list of options.
It provides an easy way to select one item from the list of items and it shows a dropdown list of all values when we click on it.
The default value of the android spinner will be the currently selected value and by using Adapter we can easily bind the items to the spinner objects.
Generally, we populate our Spinner control with a list of items by using an ArrayAdapter in our Kotlin file.


The Adapter acts as a bridge between the UI Component and the Data Source.
It converts data from the data sources into view items that can be displayed into the UI Component.
Data Source can be Arrays, HashMap, Database, etc. and UI Components can be ListView, GridView, Spinner, etc.
ArrayAdapter is the most commonly used adapter in android. 
When you have a list of single type items which are stored in an array you can use ArrayAdapter.
Likewise, if you have a list of phone numbers, names, or cities. ArrayAdapter has a layout with a single TextView.

[`onItemSelectedListener`](https://developer.android.com/reference/kotlin/android/widget/AdapterView.OnItemSelectedListener)

### 2. layout and LayoutParams

LayoutParams means the layout parameters.
LayoutParams are used by views to tell their parents how they want to be laid out.
The base LayoutParams class just describes how big the view wants to be for both width and height.

### 3. LinearLayout 

There are different types of layouts in AS on of them is the linear layout.
The type of layout will decide the pattern of our design,
All elements in AS has View as a parent class, so they called button view, text view... etc.

LinearLayout is a view group that aligns all children in a single direction, vertically or horizontally.

### 4. TableLayout

* consists of rows and columns
* starts with 0 base indexing

TableRow: A layout that arranges its children horizontally. A TableRow should always be used as a child of a TableLayout.

### 5. Fragments

A Fragment represents a reusable portion of your app's UI.
Example a portion of the screen that can change based which button you click.
It uses something called FrameLayout??

---

# AR Stuff, Sceneform

1. understand the class attributes
   1. arFragment: ArFragment 👍
   
   2. arrow1UpRenderable: Renderable 👍
   
   3. cubeRenderable: ModelRenderable 👍
   4. distanceCardViewRenderable: ViewRenderable 👍
   
   5. placedAnchors = ArrayList<Anchor>() 👍
   6. placedAnchorNodes = ArrayList<AnchorNode>() 👍
   7. midAnchors: MutableMap<String, Anchor> = mutableMapOf()
   8. midAnchorNodes: MutableMap<String, AnchorNode> = mutableMapOf()
   9. worldPosition

2. [Rendering](https://www.youtube.com/watch?v=jzaMMV6w_OE), 40min


## Understand [basics](https://www.youtube.com/watch?v=Ct1asuSts94)

* C; ArFragment: handel the requirements and premonitions
* C; Plane: type of trackable, there are also some types of planes
* C; AugmentedFace: type of trackable
* C; AugmentedImage: type of trackable
* C; Pint: type of trackable
* I; trackable: has a states and we can create by it the anchor 
* C; Anchor: is usefully to hookup objects to your scene

how the tap listener works, we tracing an array between the surface of the screen our perspective
until the plane is found, then we register a listener that is gonna tell us where the hit it

9:20, is the what is the model, each model is spited to three parts

13:10, Renderables

A; Renderables are the base class for Sceneform, has two types
C; ViewRenderable:
C; ModelRenderable: 

since creating one of the 3D objects is an extensive operation. Creating randerable is asynchronous

## ModelRenderable

C; BaseArFragment: has a method called setOnTapPlaneListener()
M; setOnTapPlaneListener(): this will get called whenever a plane in AR gets taped

the point of this code is only register the listener only when you have the resources on hand

C; Anchor: we create the hit result, we call create anchor on it,
will give a data structure that we kept updated as long the system is tracking,
will tell where in the space the user pressed

pose: is on of the Anchor attributes, is a description in space where the object is, orientation

**Anchors belong to ArCore library, while renderable belong to the Sceneform library**

Sceneform: came mush later than ArCore library
ArCore: is basically an offshoot came with Tango

C; AnchorNode: take the anchor that you get from the hit result anchor it to a node
C; Node: is the contract is the Sceneform understand

hit result -> anchor -> anchor node -> node -> Sceneform graph

$DO WE CONNECT THE ANCHOR NODE OR THE NODE THE ROOT SCENE??$

then you attach it to the root scene

C; ArSceneView: it has an attribute called "scene" tha represents the root of the scene graph

To allow the manipulation of the placed object, we add TransformableNode

C; TransformableNode: it is a child of the Node class

## ViewRenderable

also need to be done synchronously

### Rendering

* how to place an object
* relate objects to an anchor
* relate objects to each others, patent-children
* show 2D objects, like labels
* we have to decide what we gonna render is metal or not, cuz it affect how it looks like
* 

---

## More tutorials

* Play List for Android Studio Fundamentals, [link](https://www.youtube.com/playlist?list=PLQkwcJG4YTCTq1raTb5iMuxnEB06J1VHX)
* Play list for layouts, [link](https://www.youtube.com/playlist?list=PLoSj8uFDJLMFawOT2jvc1HLSgoA4iJxpo)
