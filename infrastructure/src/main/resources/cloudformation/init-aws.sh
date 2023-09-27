#!/bin/bash

set -euo pipefail

echo "========================================================================================================================="
echo "========================================== Creating Secrets ============================================================="
echo "========================================================================================================================="
awslocal secretsmanager create-secret --name /local/roadlink-core-service/rds/credentials --secret-string '{"dbuser":"root","password":"root"}'

echo "========================================================================================================================="
echo "======================================= Localstack Setup Ends ==========================================================="
echo "========================================================================================================================="