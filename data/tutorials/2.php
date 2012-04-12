<h1>2. Defining the environment</h1>
<p>The simulation environment is defined by:</p>
<ul>
  <li>A background and a wall image</li>
  <li>Maps for the places</li>
  <li>Maps for the context variables, called oeverlays</li>
</ul>
<p>At least initially, all of these are created using PNG images.</p>
<h4>Backgronud and wall maps</h4>
<p>The background image is displayed as the &quot;ground&quot; where things happen.
  It's purely cosmetic and has no influence in the simulation. The walls iamge,
  however, defines the places where agents can walk, and the places where they
  can't (walls). For the simulation to make visual sense, the walls and the background
  should match.</p>
<div class="sidepic" style="float:right"> <a href="data/tutorials/example/testland/res/map/background.png"><img class="border" src="data/tutorials/images/background.png" /><br/>
  background.png</a></div>
<p>In Testland, we create a background image using some of gimp's tools to draw
  a labyrinth. Feel free to put in your own design here. Another idea is a snapshot
  from googlemaps or a floor plan. Once you're done, save it as <a href="data/tutorials/example/testland/res/map/background.png">background.png</a>.
  This is now the base for your simulation. All the rest of images created in
  this tutorial must have the same dimensions.</p>
<p>Later on, in the next steps, you will need to calibrate this map, by providing
  the coordinates of its corners. If you captured the image from something like
  an on-line map, keep those points!.</p>
<div class="sidepic" style="float:left"> <a href="data/tutorials/example/testland/res/map/walls.png"><img class="border" src="data/tutorials/images/walls.png" /><br/>
  background.png</a></div>
<p>Next you need to define what parts of that background image are walkable.
  Create a new image, based on the background, which is black on the walkable
  areas, and white on the walls, and save it as <a href="data/tutorials/example/testland/res/map/walls.png">walls.png</a>.
  Most of the time it pays to use the magic wand to select a certain color on
  the background, create a new layer, and paint it black; then paint the background
  layer white. At any rate, check the expected <a href="data/tutorials/example/testland/res/map/walls.png">result</a>.</p>
<h4>Defining places</h4>
<p>You can define places in the WorldModel on the next step, but calculating
  the coordinates of those places can be a pain. To make your life easier, you
  can define the places using images.</p>
<p>Sometimes you want to define multiple places like, say, homes, offices, bus
  stops, and you don't necessarily care which is which, because you want each
  of your agents to live in a random house, doesn't matter which one, so we have
  places, and types of places. Now, for each <i>type</i> of place, create a white
  image, and put a single black pixel for each <i>place</i> of that type.</p>
<p>Testland is a bit of an abstract world so, instead of houses and offices,
  we define only two types: &quot;Nowhere&quot; places and &quot;Isolated&quot; places.
  The first type includes two places in the main map area, and the second, two
  places in the isolated south east corner. The images end up looking like this: <a href="data/tutorials/example/testland/res/places/Nowhere.png">Nowhere.png</a> and <a href="data/tutorials/example/testland/res/places/Isolated.png">Isolated.png</a>;
  look close, there are some dots in there!</p>
<p>Again, remember you can also define places at simulation runtime. In fact,
  a new place is created everytime you manually tell an agent where to go!</p>
<h4>Defining environment context variables</h4>
<p>Context variables can either be contained in the Agent itself (using the set
  and get methods), or can be linked to the map positions. The latter are called
  overlays, and would define, for instance the temperature, the hotspot range
  or the noise level at each position in the map. In the ContextModel (see the
  next step), you can modify this value matrix at will. In order to initialize
  it, however, we use images.</p>
<p>The value of a context variable is extracted from the pixel value at that
  point in the map. The translation is done according to the simulation config
  file, and can be one of three types:</p>
<ul>
  <li>Binary overlay: any pixel value above that threshold translates to true;
    below, it translates to false</li>
  <li>Discrete overlay: you define multiple thresholds and a tag for each interval.
    The pixel value is then fit on the right one</li>
  <li>Real overlay: the pixelvalue is directly the context variable value</li>
</ul>
<p>So, there you go. For each overlay, you'll have to create an image per overlay.
  Grayscale images are highly recommended, because the lightness of a pixel is
  proportional to the pixel value. In testland, we create overlays of all three
  types, which we will calibrate when packing the simulation. Here are the images:</p>
<table>
  <tr>
    <td>
      <div class="sidepic"><a href="data/tutorials/example/testland/res/overlays/AtCenter.png"><img class="border" src="data/tutorials/images/AtCenter.png" /><br/>AtCenter.png</a></div>
    </td>
    <td>
      <div class="sidepic"><a href="data/tutorials/example/testland/res/overlays/AtStart.png"> <img class="border" src="data/tutorials/images/AtStart.png" /><br/>AtStart.png</a></div>
    </td>
    <td>
      <div class="sidepic"><a href="data/tutorials/example/testland/res/overlays/Cell-ID.png"><img class="border" src="data/tutorials/images/Cell-ID.png" /><br/>Cell-ID.png</a></div>
    </td>
    <td>
      <div class="sidepic"><a href="data/tutorials/example/testland/res/overlays/Temperature.png"><img class="border" src="data/tutorials/images/Temperature.png" /><br/>Temperature.png</a></div>
    </td>
  </tr>
</table>
<p>Naturally, if you happen to have image maps of your context variables, do
  use them to generate the overlays.</p>
