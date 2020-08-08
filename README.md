# API for the Campsite

## Installation

##Step 1
You need to edit the .env file and change the environment variable POSTGRES_HOST_DATA_DIR to an accessible temp directory on your local machine

##Step 2
- Give the script `init_project.sh` executable permissions using `chmod a+x init_project.sh`*
- Run the `init_project.sh` bash script located in the root directory which will set everything up. If it doesn't work, the manual steps are listed below
    
    Only run this once. If it doesn't work successfully then go into each of the 2 project folders and follow the manual installation instructions of the README.md files

***
#### Manual installation of `init_project.sh` script commands
*Do this only if the `init_project.sh` script fails*


- When inside the current directory of this project where the Dockerfile is, you must first build the jar of the project
  
  Run the command: `mvn package`
  
- Then after that is successful you build the docker image which will be used by docker-compose.
 
  Run the command:  `docker build -t upgrade-campsite-java-api .`
 which will create the docker image 'upgrade-campsite-java-api' 
 
 