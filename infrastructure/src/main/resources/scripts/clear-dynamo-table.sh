#!/bin/bash

# Nombre de la tabla
table_name="RoadlinkCore"

# Obtener la lista de claves primarias de la tabla
keys=$(awslocal dynamodb scan --table-name "$table_name" --select "ALL_ATTRIBUTES" --output json \
    | grep -oP '"S": "\K[^"]+' | tr '\n' ' ')

# Eliminar cada elemento de la tabla
for key in $keys; do
    awslocal dynamodb delete-item --table-name "$table_name" --key '{"EntityId": {"S": "'"$key"'"}}'
done