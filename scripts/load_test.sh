#!/bin/bash

# DBPulse Load Testing Script
# Generates realistic traffic to populate monitoring metrics

set -e

BASE_URL="http://localhost:8080/api"
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   DBPulse Load Testing Script${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# Wait for application to be ready
echo -e "${GREEN}Waiting for application to be ready...${NC}"
while ! curl -s http://localhost:8080/actuator/health > /dev/null; do
    sleep 2
    echo -n "."
done
echo -e "\n${GREEN}Application is ready!${NC}\n"

# Function to create a client
create_client() {
    local i=$1
    curl -s -X POST "${BASE_URL}/clients" \
        -H "Content-Type: application/json" \
        -d "{
            \"firstName\": \"User${i}\",
            \"lastName\": \"Test\",
            \"email\": \"user${i}@dbpulse.test\",
            \"phone\": \"+1-555-000-${i}\",
            \"address\": \"${i} Main Street\",
            \"city\": \"TestCity\",
            \"country\": \"TestCountry\"
        }" > /dev/null
}

# Function to create a product
create_product() {
    local i=$1
    local categories=("Electronics" "Clothing" "Books" "Food" "Toys")
    local category=${categories[$((i % 5))]}
    
    curl -s -X POST "${BASE_URL}/products" \
        -H "Content-Type: application/json" \
        -d "{
            \"name\": \"Product ${i}\",
            \"description\": \"Test product ${i} description\",
            \"sku\": \"SKU-${i}-TEST\",
            \"price\": $((10 + i * 5)),
            \"stockQuantity\": $((50 + i * 10)),
            \"category\": \"${category}\"
        }" > /dev/null
}

# Function to create an order
create_order() {
    local client_id=$1
    local product_id=$2
    local quantity=$((1 + RANDOM % 5))
    
    curl -s -X POST "${BASE_URL}/orders" \
        -H "Content-Type: application/json" \
        -d "{
            \"clientId\": ${client_id},
            \"items\": [
                {
                    \"productId\": ${product_id},
                    \"quantity\": ${quantity}
                }
            ],
            \"shippingAddress\": \"${client_id} Shipping Street\",
            \"notes\": \"Test order from load testing script\"
        }" > /dev/null
}

# Create Clients
echo -e "${GREEN}Creating 50 clients...${NC}"
for i in {1..50}; do
    create_client $i
    if [ $((i % 10)) -eq 0 ]; then
        echo -n "."
    fi
done
echo -e " ${GREEN}Done!${NC}"

# Create Products
echo -e "${GREEN}Creating 30 products...${NC}"
for i in {1..30}; do
    create_product $i
    if [ $((i % 10)) -eq 0 ]; then
        echo -n "."
    fi
done
echo -e " ${GREEN}Done!${NC}"

# Create Orders (generates the most interesting metrics)
echo -e "${GREEN}Creating 100 orders...${NC}"
for i in {1..100}; do
    client_id=$((1 + RANDOM % 50))
    product_id=$((1 + RANDOM % 30))
    create_order $client_id $product_id
    
    if [ $((i % 20)) -eq 0 ]; then
        echo -n "."
    fi
    
    # Add small delay to simulate realistic traffic
    sleep 0.1
done
echo -e " ${GREEN}Done!${NC}"

# Query patterns to generate read traffic
echo -e "${GREEN}Generating read traffic...${NC}"
for i in {1..50}; do
    # Get random client
    curl -s "${BASE_URL}/clients/$((1 + RANDOM % 50))" > /dev/null
    
    # Search clients
    curl -s "${BASE_URL}/clients/search?term=User" > /dev/null
    
    # Get random product
    curl -s "${BASE_URL}/products/$((1 + RANDOM % 30))" > /dev/null
    
    # Get products by category
    local categories=("Electronics" "Clothing" "Books" "Food" "Toys")
    local cat=${categories[$((RANDOM % 5))]}
    curl -s "${BASE_URL}/products/category/${cat}" > /dev/null
    
    if [ $((i % 10)) -eq 0 ]; then
        echo -n "."
    fi
    sleep 0.05
done
echo -e " ${GREEN}Done!${NC}"

echo
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}Load testing complete!${NC}"
echo -e "${BLUE}========================================${NC}"
echo
echo "Metrics should now be visible in:"
echo "  - Prometheus: http://localhost:9090"
echo "  - Grafana: http://localhost:3000 (admin/admin)"
echo
echo "Total created:"
echo "  - 50 Clients"
echo "  - 30 Products"
echo "  - 100 Orders"
echo "  - 250+ Read operations"
echo
