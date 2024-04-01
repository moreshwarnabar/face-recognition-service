# face-recognition-service

This app will provide an image recognition service to users. This is a part of the coursework for the CSE-546 Cloud Computing class at Arizona State University.

## Description

An elastic application that can automatically scale out and in on-demand and cost-effectively by using the IaaS cloud. Specifically, this application will be built using the IaaS resources from Amazon Web Services (AWS). AWS is the most widely used IaaS provider and offers a variety of compute, storage, and message services. The application will offer a meaningful cloud service to users, namely, it will process requests for face recognition by accepting an image as input, feeding the image into a pre-trained image classification model, and returning the classification result.

## Tech Stack

- Spring Boot
- AWS Java SDK v2

## Architecture

The cloud service will provide an image recognition service to users, by using cloud resources to perform deep learning on images provided by the users. The deep learning model was provided by the instructor, and the application uses this model to provide the service and meet the following typical requirements for a cloud application:

The service takes images received from users as input and performs image recognition on these images using the provided deep learning model. It returns the recognition result (the top 1 result from the provided model) as output to the users. The input is a .png file, and the output is the prediction result. For example, the user uploads an image named “test_0.JPEG”. For the above request, the output should be “Paul” in plain text.

To facilitate the testing, a standard image dataset was provided, along with the expected output for each image.

The service is able to handle multiple requests concurrently. It automatically scales out when the request demand increases, and automatically scale in when the demand drops. Because we have limited resources from the free tier, the service uses no more than 20 instances, and it queues all the pending requests when it reaches this limit. When there is no request, the service uses no instance of the App tier. When the requests are within 1 to 20, it should scale up linearly.

The service automatically scales using the logic running in Web tier as "ScaleController" by monitoring and scaling based on the depth of the Request SQS Queue and the number of running instances of the App tier.

All the inputs (images) and outputs (recognition results) are stored in S3 for persistence.

The app handles all the requests as fast as possible, and it does not miss any requests. The recognition requests are all correct.

## External Resources

- [Model](https://github.com/visa-lab/CSE546-Cloud-Computing/tree/main/model)
- [Test Images and Results](https://github.com/visa-lab/CSE546-Cloud-Computing/tree/main/dataset)
