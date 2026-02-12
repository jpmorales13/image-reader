# Image Repository API

This API:
- Receives an image thorugh a REST Call
- Uses Google AI Vision to identify all elements in the image
- Elements found in image are stored in Postgres DB
- Responds to the call with all the elements in the image
- Client can send requests to retrieve elements in an image
- Client can send requests to find all images with a certain element in it.
