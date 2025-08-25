# Consideration for Storage

## Local File

For this PoC we are going to store the file locally. But in a real world application we would store it in a GCP Bucket
or AWS S3.

## Database

As the file will be stored in a file system that is easily manageable we will handle the Metadata around the files in a
Postgres Database. This would minimise the Database and being able to process the files easily.

## How to run

In order to run this, run the Docker Compose file
