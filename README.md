# 🌲 PEPSE: Precise Environmental Procedural Simulator Extraordinaire

PEPSE is a Java-based 2D game developed by [**Noam Kimhi**](https://github.com/noam-kimhi) and [**Or Forshmit**](https://github.com/OrF8) as part of the course [**OOP**](https://shnaton.huji.ac.il/index.php/NewSyl/67125/2/2025/) at [**The Hebrew University of Jerusalem**](https://en.huji.ac.il/). \
This project focuses on creating a side-scrolling game environment with procedurally generated terrain and interactive elements. 
> 🎓 Final Grade: **100**

# 🚀 Features
- **Procedural Terrain Generation**: Dynamically creates terrain as the player moves through the game world.
- **Day-Night Cycle**: Implements a realistic day-night cycle affecting the game's lighting and ambiance.
- **Interactive Entities**: Includes various entities such as avatars and obstacles that interact within the game environment.
- **Physics Integration**: Incorporates physics-based mechanics for realistic movement and interactions.

# 🛠️ Getting Started
## Prerequisites
- Java Development Kit (JDK) 22 or higher
- An Integrated Development Environment (IDE) such as IntelliJ IDEA or Eclipse (Optional).
## Installation
1. Clone the repository:
   ````
   git clone https://github.com/OrF8/PEPSE.git
   cd PEPSE
   ````
2. If using an IDE - Import the project into your IDE:
   - For IntelliJ IDEA:
     - Open IntelliJ IDEA.
     - Select "Open" and choose the `PEPSE` directory.
     - IntelliJ will detect the project structure and set it up accordingly.
   - For Eclipse:
     - Open Eclipse.
     - Select "File" > "Import" > "Existing Projects into Workspace".
     - Choose the PEPSE directory and click "Finish".
3. Add DanoGameLab as a dependency (see [credits](https://github.com/OrF8/PEPSE/edit/main/README.md#credits).
4. Build and run the project:
   - Ensure DanoGameLab is included in your classpath or set up as a library dependency.
   - Compile and run the `PepseGameManager.java` class to start the game.

# 🎮 Gameplay
- **Navigation**: Control the avatar to explore the procedurally generated world.
- **Interaction**: Engage with various entities and obstacles within the environment.
- **Environment**: Experience the dynamic day-night cycle that influences gameplay and visuals.

# Credits
- This work was made using the [**DanoGameLab**](https://danthe1st.itch.io/danogamelab) library by Dan Nirel.
- All assets are created by Dan Nirel.

🗂️ Project Structure
````
PEPSE/
├── src/                                   # Source code directory
│   └── pepse/                             # Main package
│       ├── world/                         # Terrain and environment-related classes
│       ├── util/                          # Utility classes
│       └── PepseGameManager.java          # Additional packages and classes
├── assets/                                # Game assets
├── README.md                              # Project documentation
└── LICENSE                                # MIT License
````

# 📄 License
This project is licensed under the MIT License – see the [**LICENSE**](https://github.com/OrF8/PEPSE/blob/main/LICENSE) file for details














