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
    "ProfilePhotoUrl": {"S": "https://profile.photo.com"},
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
    "ProfilePhotoUrl": {"S": "https://profile.photo.com"},
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
    "ProfilePhotoUrl": {"S": "https://profile.photo.com"},
    "BirthDay": {"S": "06/12/1991"},
    "Gender": {"S": "Male"},
    "UserName": {"S": "felix.reyero"}
  }'
echo "========================================================================================================================="
echo "======================================= Localstack Setup Ends ==========================================================="
echo "========================================================================================================================="
