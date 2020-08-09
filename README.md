# API for the Campsite

## Installation

## Step 1
Clone this github project to your machine

## Step 2
You need to edit the .env file (hidden file in the root directory of this project) and change the environment variable POSTGRES_HOST_DATA_DIR to an accessible temp directory on your local machine

## Step 3
- Give the script `init_project.sh` executable permissions using `chmod a+x init_project.sh`
- Run the `init_project.sh` bash script located in the root directory which will set everything up. If it doesn't work, the manual steps are listed below
    
    *Only run this once. If it doesn't work successfully then go into each of the 2 project folders and follow the manual installation instructions of the README.md files*

## Step 4
Run the command `docker-compose up` to start everything up. Then query the end-points 

***
#### Manual installation of `init_project.sh` script commands
*Do this only if the `init_project.sh` script fails*

- First make sure the docker containers `campsite-java-api` and `campsitepg-db` are removed using `docker rm [containerName]`
- Then remove the two docker images (if they exist) `campsite-java-api` and `campsite_pg` using `docker rmi [imageName]`

- Change to the `upgrade-campsite-postgresdb` and run the `docker build -t campsite_pg .`


- Change to the `upgrade-campsite` directory for the Java API. You must first build the jar of the project
  
  Run the command: `mvn package`
  
- Then after that is successful you build the docker image which will be used by docker-compose.
 
  Run the command:  `docker build -t upgrade-campsite-java-api .` in the `upgrade-campsite` directory, 
 which will create the docker image 'upgrade-campsite-java-api' 
 
 
 
  ***
  
  ## End-points
  - /api/registration   <-- the root mapping
  
  - [GET] /api/registration/availability    <-- gives you the availability of the passed date range. If no range passed, then default is 1 month.
  query parameters to pass
  
  - [POST] /api/registration/reserve  <-- this reserves the dates passed via `to` and `from` in the format YYYY-MM-DD. In the body of the request, you must pass the payload as mentioned below in the example.  
  
  - [PUT] /api/registration/{bookingId}  <-- this is for modifying an existing reservation with the booking ID. Dates passed via `to` and `from` in the format YYYY-MM-DD will replace the existing dates of the reservation 
  
  - [DELETE] /api/registration/{bookingId}  <-- This is for cancelling a reservation
  
  
  
  ### Example payload when doing POST or PUT calls
 
  {
     "name" :"Shaun",
     "email":"Shaun@shaun.com"
  }