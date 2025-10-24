# CHANGELOGS
This file contains all important changelogs for QuickStart

### 9/28/2025
#### Added
+ Service registry initialization logic
+ Service registry creation and manipulation logic

### 10/3/2025
#### Added
+ Added process handling logic
+ Added core profile logic
+ Added temp file creation, run and deletion logic

### 10/4/2025
#### Added
+ Added more descriptive error messages

#### Modified
+ Modified profile creation decision logic
+ Modified registry methods to only handle base paths

### 10/5/2025
#### Added
+ Created command structure and integrated pico cli to project
+ Added more robust error handling around the code

### 10/6/2025
#### Added
+ Added installation scripts for both windows and unix users

#### Fixed 
+ Fixed an issue where the registry handler class didn't load before it threw an exception


### 10/24/2025
#### Fixed
+ Fixed an issue where profiles we're inaccessible even though they existed

#### Modified
+ Made profile creation consistent across all methods
+ Introduced an enum instead of a boolean variable to keep the code readable
+ Modified `findSimilarProfiles()` to run everytime a user inputs a non-existent profile name


