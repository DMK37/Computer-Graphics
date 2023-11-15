# Polygon Editor
Specification:
Ability to add new polygon, delete and edit
When editing:

  - moving a vertex
  - deleting a vertex
  - adding a vertex in the center of a selected edge
  - moving the entire edge
  - moving the entire polygon

Adding constraints (relations) for the selected edge:

possible constraints:
  - horizontal edge, vertical edge
  - two adjacent edges can not be both vertical or both horizontal
  - adding a vertex on an edge or removing a vertex - removes restrictions on "adjacent" edges
  - set constraints are visible (as appropriate "icons") at the edge center
  - it should be possible to remove relations

Enabling/disabling offset polygon.

Drawing of segments - library algorithm and own implementation (alg. Bresenham) - radiobutton

Aliasing using Xiaolin Wu's algorithm and serialization, deserialization of scene (using xml)
