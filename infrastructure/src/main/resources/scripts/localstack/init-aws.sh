#!/bin/bash

#set -euo pipefail

echo "========================================================================================================================="
echo "========================================== Creating Secrets ============================================================="
echo "========================================================================================================================="
awslocal ssm put-parameter \
  --name /local/roadlink-core-service/dynamo/credentials \
  --value '{"endpoint":"http://localstack:4566", "region": "us-west-2"}' \
  --type "SecureString" \
  --output table
awslocal ssm put-parameter \
  --name /local/roadlink-core-service/google/credentials \
  --value '{"client_id":"REPLACE_ME"}' \
  --type "SecureString" \
  --output table
echo "========================================================================================================================="
echo "======================================= Creating Dynamo Table ==========================================================="
echo "========================================================================================================================="
awslocal cloudformation create-stack \
  --stack-name "core-dynamo-table-stack" \
  --template-body file://core-dynamo-table.yml \
  --region "${AWS_REGION}" \
  --output table
echo "========================================================================================================================="
echo "======================================= Saving Initial Users ============================================================"
echo "========================================================================================================================="
awslocal dynamodb put-item \
  --table-name RoadlinkCore \
  --item '{
    "EntityId": {"S": "EntityId#User"},
    "Id": {"S": "123e4567-e89b-12d3-a456-426614174001"},
    "CreatedDate": {"S": "2024-02-01T23:04:52.499Z"},
    "FirstName": {"S": "Jorge Javier"},
    "LastName": {"S": "Cabrera Vera"},
    "Email": {"S": "jorge.cabrera@roadlink.com"},
    "Friends": {"SS": ["123e4567-e89b-12d3-a456-426614174002", "123e4567-e89b-12d3-a456-426614174003"]},
    "ProfilePhotoUrl": {"S": "https://lh3.googleusercontent.com/a/ACg8ocIb2KRpXYKpnlkY5Ayh_e0JwzxhAgr10S-weL7WuZXorjA=s96-c"},
    "BirthDay": {"S": "06/12/1991"},
    "Gender": {"S": "Male"},
    "UserName": {"S": "jorge.cabrera"}
  }'

awslocal dynamodb put-item \
  --table-name RoadlinkCore \
  --item '{
    "EntityId": {"S": "EntityId#User"},
    "Id": {"S": "123e4567-e89b-12d3-a456-426614174002"},
    "CreatedDate": {"S": "2024-02-01T23:04:52.499Z"},
    "FirstName": {"S": "Martin"},
    "LastName": {"S": "Bosch"},
    "Email": {"S": "martin.bosch@roadlink.com"},
    "Friends": {"SS": ["123e4567-e89b-12d3-a456-426614174001", "123e4567-e89b-12d3-a456-426614174003"]},
    "ProfilePhotoUrl": {"S": "https://lh3.googleusercontent.com/a/ACg8ocJW5g-yavaNzKPZcF-U8-W5zGfIQdww2mOcyDq_48xfdHE=s96-c"},
    "BirthDay": {"S": "06/12/1991"},
    "Gender": {"S": "Male"},
    "UserName": {"S": "martin.bosch"}
  }'

awslocal dynamodb put-item \
  --table-name RoadlinkCore \
  --item '{
    "EntityId": {"S": "EntityId#User"},
    "Id": {"S": "123e4567-e89b-12d3-a456-426614174003"},
    "CreatedDate": {"S": "2024-02-01T23:04:52.499Z"},
    "FirstName": {"S": "Felix"},
    "LastName": {"S": "Reyero"},
    "Email": {"S": "felix.reyero@roadlink.com"},
    "Friends": {"SS": ["123e4567-e89b-12d3-a456-426614174001", "123e4567-e89b-12d3-a456-426614174002"]},
    "ProfilePhotoUrl": {"S": "https://lh3.googleusercontent.com/a/ACg8ocJl4rpUUhbBzXDZDx7aFarTkVfxI0vPKWAbasFr-zHGLL0=s96-c"},
    "BirthDay": {"S": "06/12/1991"},
    "Gender": {"S": "Male"},
    "UserName": {"S": "felix.reyero"}
  }'

awslocal dynamodb put-item \
  --table-name RoadlinkCore \
  --item '{
    "EntityId": {"S": "EntityId#User"},
    "Id": {"S": "123e4567-e89b-12d3-a456-426614174004"},
    "CreatedDate": {"S": "2024-02-01T23:04:52.499Z"},
    "FirstName": {"S": "Roadlink Tech"},
    "LastName": {"S": "Admin"},
    "Email": {"S": "roadlinktech@gmail.com"},
    "Friends": {"SS": ["123e4567-e89b-12d3-a456-426614174002", "123e4567-e89b-12d3-a456-426614174003"]},
    "ProfilePhotoUrl": {"S": "https://lh3.googleusercontent.com/a/ACg8ocI_U8wQyvqlmaJaKyBakIOImnXZ8viH3A4iJRDHOJVQ420wla8=s96-c"},
    "BirthDay": {"S": "06/12/1991"},
    "Gender": {"S": "Male"},
    "UserName": {"S": "roadlink"}
  }'

awslocal dynamodb put-item \
  --table-name RoadlinkCore \
  --item '{
    "EntityId": {"S": "EntityId#GoogleUser"},
    "Id": {"S": "105844299497198425288"},
    "CreatedDate": {"S": "2024-02-01T23:04:52.499Z"},
    "UserId": {"S": "123e4567-e89b-12d3-a456-426614174004"}
  }'

awslocal dynamodb put-item \
  --table-name RoadlinkCore \
  --item '{
    "EntityId": {"S": "EntityId#FeedbackSolicitude"},
    "Id": {"S": "83ac1b75-8105-46ba-acdf-97037c340cd0"},
    "TripLegId": {"S": "09ef9d79-cf1d-4401-a4c3-49cc78c8a2de"},
    "ReceiverId": {"S": "123e4567-e89b-12d3-a456-426614174004"},
    "ReviewerId": {"S": "123e4567-e89b-12d3-a456-426614174002"},
    "CreatedDate": {"S": "2024-02-01T23:04:52.499Z"},
    "FeedbackSolicitudeStatus": {"S": "COMPLETED"}
  }'

awslocal dynamodb put-item \
  --table-name RoadlinkCore \
  --item '{
    "EntityId": {"S": "EntityId#Feedback"},
    "Id": {"S": "f399ff9b-54dd-4a12-9ab9-c361eef450c8"},
    "TripLegId": {"S": "09ef9d79-cf1d-4401-a4c3-49cc78c8a2de"},
    "ReceiverId": {"S": "123e4567-e89b-12d3-a456-426614174004"},
    "Comment": {"S": "El viaje estuvo confortable"},
    "Rating": {"N": "4"},
    "ReviewerId": {"S": "123e4567-e89b-12d3-a456-426614174002"},
    "CreatedDate": {"S": "2024-02-01T23:04:52.499Z"}
}'

awslocal dynamodb put-item \
  --table-name RoadlinkCore \
  --item '{
    "EntityId": {"S": "EntityId#FriendshipSolicitude"},
    "Id": {"S": "916844ea-7f65-44e4-bf07-aae89ffa6f76"},
    "RequesterId": {"S": "123e4567-e89b-12d3-a456-426614174001"},
    "SolicitudeStatus": {"S": "PENDING"},
    "AddressedId": {"S": "123e4567-e89b-12d3-a456-426614174004"},
    "CreatedDate": {"S": "2024-02-01T23:04:52.499Z"}
}'

awslocal dynamodb put-item \
  --table-name RoadlinkCore \
  --item '{
    "EntityId": {"S": "EntityId#Vehicle"},
    "Id": {"S": "b85df607-16cf-4da2-8f2e-51baa90a1748"},
    "Brand": {"S": "Ford"},
    "IconUrl": {"S": "iconUrl"},
    "DriverId": {"S": "123e4567-e89b-12d3-a456-426614174004"},
    "Capacity": {"N": "4"},
    "Color": {"S": "Marrón"},
    "Model": {"S": "Territory"},
    "LicencePlate": {"S": "AF254BR"},
    "CreatedDate": {"S": "2024-02-01T23:04:52.499Z"}
}'

echo "========================================================================================================================="
echo "======================================= Localstack Setup Ends ==========================================================="
echo "========================================================================================================================="
