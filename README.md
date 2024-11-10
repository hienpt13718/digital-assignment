### Database schema
```sql
-- Users/Authentication
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,  -- ADMIN, OPERATOR, CUSTOMER
    status VARCHAR(20) NOT NULL,  -- UNDER_VERIFY, ACTIVE, INACTIVE, DELETED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Shops
CREATE TABLE shops (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    max_queue_size INTEGER NOT NULL,
    number_of_queues INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    opening_day_of_week INTEGER NOT NULL,
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL
);

-- Menu Items
CREATE TABLE menu_items (
    id BIGSERIAL PRIMARY KEY,
    shop_id BIGINT REFERENCES shops(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN DEFAULT true
);

-- Customers
CREATE TABLE customers (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    mobile_number VARCHAR(20) UNIQUE NOT NULL,
    address VARCHAR(255),
    loyalty_score INTEGER DEFAULT 0
);

-- Orders
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id UUID REFERENCES customers(id),
    shop_id BIGINT REFERENCES shops(id),
    status VARCHAR(20) NOT NULL, -- PENDING, IN_QUEUE, PROCESSING, COMPLETED, CANCELLED
    queue_number INTEGER,
    queue_position INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Order Items
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    menu_item_id BIGINT REFERENCES menu_items(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL
);
```