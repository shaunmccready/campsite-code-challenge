# Spring Boot API for the Campsite

## Installation

- Give the script `init_project.sh` executable permissions using `chmod a+x init_project.sh`
- Run the `init_project.sh` bash script located in the root directory which will set everything up. If it doesn't work, the manual steps are listed below




***
#### Manual installation of `init_project.sh` script commands
*Do this only if the `init_project.sh` script fails. Its best to follow the instructions in the README.md in the root directory instead of these. This is a shortcut if you know what you're doing*

- When inside the current directory of this project where the Dockerfile is, you must first build the jar of the project
  
  Run the command: `mvn package`
  
- Then after that is successful you build the docker image which will be used by docker-compose.
 
  Run the command:  `docker build -t campsite-java-api .`
 which will create the docker image 'upgrade-campsite-java-api' 
 
 