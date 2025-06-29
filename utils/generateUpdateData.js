const fs = require('fs');
const path = require('path');
const { faker } = require('@faker-js/faker');

const INPUT_FILE = '../src/main/resources/db/changelog/data.sql';
const NUM_PRODUCTS = 50;

// Helper to escape single quotes in SQL strings
const esc = (str) => str.replace(/'/g, "''");

// Read and parse max order ID from data.sql
const sql = fs.readFileSync(path.join(__dirname, INPUT_FILE), 'utf-8');

const orderIdMatches = [...sql.matchAll(/INSERT INTO\s+"order"\s*\(id,[^)]+\)\s+VALUES\s*\((\d+),/g)];
const maxOrderId = orderIdMatches.reduce((max, match) => Math.max(max, parseInt(match[1])), 0);

// Create products
for (let i = 1; i <= NUM_PRODUCTS; i++) {
    const description = esc(faker.commerce.productName());
    console.log(`INSERT INTO product (id, description) VALUES (${i}, '${description}');`);
}

// Create order-product links
for (let orderId = 1; orderId <= maxOrderId; orderId++) {
    // Random number of products to generate - range(1-5)
    const numLinks = Math.floor(Math.random() * 5) + 1;
    const productIds = new Set();

    while (productIds.size < numLinks) {
        productIds.add(Math.ceil(Math.random() * NUM_PRODUCTS));
    }

    for (const productId of productIds) {
        console.log(`INSERT INTO order_product (order_id, product_id) VALUES (${orderId}, ${productId});`);
    }
}
