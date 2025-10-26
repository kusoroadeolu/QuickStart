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


### [v1.0.1A] 10/25/2025
#### Fixed
+ Fixed an issue where profiles were inaccessible even though they existed.

#### Modified
+ Made profile creation consistent across all methods
+ Introduced an enum instead of a boolean variable to keep the code readable
+ Modified `findSimilarProfiles()` to run everytime a user inputs a non-existent profile name


### [v1.0.1B] 10/26/2025
#### Modified
+ Modified profile runs to run from the profile directory to ensure consistency when running profiles
+ Updated the installation scripts to replace the old jar files in the quickstart home path 
+ Modified the docker command to use the name of the profile as the project/container name
