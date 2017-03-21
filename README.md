# MultipartFileUploader
This project demonstrastes a way to utilize full network bandwidth available while transfering a file over network. Server starts to upload a file on to the client as soon as client is connected. The file is broken into multiplie parts and each parts is sent to client parallely. At first a metadata bytestream is sent to the client for client to make appropriate connections to the server in order to receive the full file.
Then follows a series of parallel threads transmitting parts of the same file on to the client mentioned location.
