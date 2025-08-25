# Getting Started

## Upload file

Upload a file to the API.

**Endpoint:**

`POST /files/upload`

**Parameters:**

- `file` (required): The file
- `userId` (required): ID of the user uploading the file
- `visibility` (required): File visibility (PRIVATE/PUBLIC)
- `tags` (optional): Comma-separated list of tags. No more than 5 tags allowed.
- `fileName` (optional): Name of the file

**Response:**

- `fileId`: ID of the file
- `message`: Message saying if the File was uploaded or Duplication existed.
- `downloadLink`: Link to download the file

## Download file

Download a file owned by the User or Public.

**Endpoint:**

`GET /files/download/{fileId}`

**Parameters:**

- `fileId` (required): ID of the file
- `userId` (required): ID of the user uploading the file

**Response:**

- `file`: File to be downloaded.

## List files

Provide a List of Files.
All Public files
All Private files for the User making the request.
This endpoint is paginated.

**Endpoint:**

`GET /files`

**Parameters:**

- `userId` (required): ID of the user uploading the file

**Response:**

- `fileId`: ID of a file
- `fileName`: Name of a file
- `fileSize`: Size of a file
- `filePath`: Path of a file
- `tags`: List of tags
- `updatedAt`: When file was uploaded

## Delete file

Deleting a file

**Endpoint:**

`DELETE /files/{fileId}`

**Parameters:**

- `file` (required): The file

**Body:**

- `fileName` (required): Name of file to be deleted
- `userId` (required): Owner of the file

**Response:**

200 if the file was deleted

## Update file

Updating a file.
For now, only the file name can be updated

**Endpoint:**

`DELETE /files/update/{fileId}`

**Parameters:**

- `file` (required): The file

**Body:**

- `fileName` (required): Name of file to be deleted
- `userId` (required): Owner of the file

**Response:**

200 if the file was updated
