# Default Generators
<link-summary>Reference guide for built-in particle shape generators</link-summary>

Generators are responsible for producing the initial spatial coordinates for a particle effect relative to the origin. Below is a list of all built-in static factory methods available in the `Generators` utility class.

### Point
**Method:** `Generators.point()`

Generates a single point exactly at the origin `(0, 0, 0)`.

*(No parameters required)*

---

### Line
**Method:** `Generators.line()`

Generates a straight line of particles between two specified points.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>start</code></td>
<td>The starting <code>Vector</code> coordinate.</td>
</tr>
<tr>
<td><code>end</code></td>
<td>The ending <code>Vector</code> coordinate.</td>
</tr>
<tr>
<td><code>step</code></td>
<td>The distance gap between each individual particle along the line.</td>
</tr>
</table>

---

### Circle
**Method:** `Generators.circle()`

Generates a flat circle on the XZ plane.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>radius</code></td>
<td>The horizontal radius of the circle.</td>
</tr>
<tr>
<td><code>points</code></td>
<td>The total number of particles comprising the circumference.</td>
</tr>
</table>

---

### Square
**Method:** `Generators.square()`

Generates a flat square outline on the XZ plane.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>size</code></td>
<td>The side length of the square.</td>
</tr>
<tr>
<td><code>pointsPerSide</code></td>
<td>The number of particles plotted along each edge.</td>
</tr>
</table>

---

### Sphere
**Method:** `Generators.sphere()`

Generates a 3D sphere using a Fibonacci spiral distribution for even particle spacing.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>radius</code></td>
<td>The radius of the sphere.</td>
</tr>
<tr>
<td><code>points</code></td>
<td>The total number of particles defining the sphere's surface.</td>
</tr>
</table>

---

### Cube
**Method:** `Generators.cube()`

Generates the 3D wireframe outline of a cube.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>size</code></td>
<td>The edge length of the cube.</td>
</tr>
<tr>
<td><code>resolution</code></td>
<td>The number of particles defining each edge.</td>
</tr>
</table>

---

### Pyramid
**Method:** `Generators.pyramid()`

Generates a 3D pyramid shape with a square base.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>size</code></td>
<td>The width of the pyramid's base.</td>
</tr>
<tr>
<td><code>height</code></td>
<td>The total vertical height of the pyramid.</td>
</tr>
<tr>
<td><code>resolution</code></td>
<td>The particle density per horizontal layer.</td>
</tr>
</table>

---

### Helix
**Method:** `Generators.helix()`

Generates a 3D helix (spring) shape.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>radius</code></td>
<td>The radius of the helix curve.</td>
</tr>
<tr>
<td><code>height</code></td>
<td>The total vertical height of the structure.</td>
</tr>
<tr>
<td><code>turns</code></td>
<td>The number of complete 360-degree rotations the spiral makes.</td>
</tr>
<tr>
<td><code>points</code></td>
<td>The total number of particles.</td>
</tr>
<tr>
<td><code>speed</code></td>
<td>The dynamic rotational speed offset (for animated spinning).</td>
</tr>
</table>

---

### Image
**Method:** `Generators.fromImage()`

Replicates a 2D `BufferedImage` by mapping its non-transparent pixels to 3D space.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>image</code></td>
<td>The source <code>BufferedImage</code> to convert.</td>
</tr>
<tr>
<td><code>size</code></td>
<td>The maximum world-space size the image should occupy.</td>
</tr>
<tr>
<td><code>density</code></td>
<td>The pixel skip rate (e.g., <code>1</code> for every pixel, <code>2</code> for every other pixel).</td>
</tr>
</table>

---

### Text
**Method:** `Generators.text()`

Converts a string of text into a particle-based layout.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>text</code></td>
<td>The string of text to display.</td>
</tr>
<tr>
<td><code>fontName</code></td>
<td>The system font name to render with.</td>
</tr>
<tr>
<td><code>style</code></td>
<td>The font style (e.g., <code>Font.BOLD</code>).</td>
</tr>
<tr>
<td><code>color</code></td>
<td>(Optional) A <code>ColorProvider</code> for gradient or solid text coloring. Defaults to white.</td>
</tr>
<tr>
<td><code>fontSize</code></td>
<td>The internal rasterization font size.</td>
</tr>
<tr>
<td><code>size</code></td>
<td>The maximum world-space width/height of the generated text.</td>
</tr>
</table>