# DELHI METRO NAVIGATION SYSTEM

## Overview:

- **Objective:** Develop a Java program to find the shortest metro route and calculate the fare between two stations in the Delhi Metro.
- **Features:**
  - Takes input for source and destination stations.
  - Displays fare and shortest route.
  - Includes a metro map for better navigation.

## Implementation:

- **Data Structures:**
  - **Graph:** Represents the metro network with stations as nodes and connections as edges.
    - **Nodes:** Contain station information (name, corridor, connecting lines).
    - **Edges:** Represent distances between stations with costs equal to these distances.
  - **Heap:** Used for efficient route calculations.
- **Algorithms:** Utilizes Dijkstra's algorithm to determine the shortest path and calculate fare based on the total distance.

## Files:

- `Graph_M.java`: Contains the core functionality and algorithms.
- `Heap.java`: Implements the heap data structure.
