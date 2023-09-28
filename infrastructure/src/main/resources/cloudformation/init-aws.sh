#!/bin/bash

set -euo pipefail

echo "========================================================================================================================="
echo "========================================== Creating Secrets ============================================================="
echo "========================================================================================================================="

awslocal ssm put-parameter \
  --name /local/roadlink-core-service/rds/credentials \
  --value '{"dbuser":"root","password":"root"}' \
  --type "SecureString"

echo "========================================================================================================================="
echo "======================================= Localstack Setup Ends ==========================================================="
echo "========================================================================================================================="
