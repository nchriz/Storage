# Consideration for Storage

## Local File

For this PoC we are going to store the file locally. But in a real world application we would store it in a GCP Bucket
or AWS S3.

How the file is stored is of no importance, only when they download the file it should be in the file name they have specificed

Files are also stored for each User. The structure is as followed

userID/UniqueID_originalFileName

When a User tries to upload a file that clashes with the Content the User has already uploaded, or there is a file with similar name, they will be allowed to do so.

There isn't a restriction to change the name to an already existing file thus if we mainain unique reference to files we allow this.

Each Interactions with a file is through the fileID

## Database

As the file will be stored in a file system that is easily manageable we will handle the Metadata around the files in a
Postgres Database. This would minimise the Database and being able to process the files easily.

## How to run

In order to run this, run the Docker Compose file
