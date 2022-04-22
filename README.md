# Parallel-Random-Search-Tree

A random search tree is created to help find paths in a 2D environment.

A room square of side length 1 is created. The room contains obstacle squares of side length 0.05.

A random point in the room (not inside an obstacle) is generated and set as the root tree node.

Tree nodes (points) are expanded by selecting another random point in the room such that there are no obstacles intersecting with the straight line between both points.

I created my own "executor service" which achieves good parallelization by minimizing lock contention.
The expansion tasks are intentionally kept short (if they take a long time then there will be little lock contention).</br></br>


./q1 n b r t</br></br>

n = number of nodes in the tree to generate. n >= 1</br>
b = maximum amount of children per node. b >= 2</br>
r = max distance betwen a node and its child. 0 < r < 1</br>
t = number of threads. t >= 1
