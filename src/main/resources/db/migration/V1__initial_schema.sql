CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL,

    email VARCHAR(255) NOT NULL UNIQUE,

    created_at TIMESTAMP,

    updated_at TIMESTAMP
);

CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL,

    created_at TIMESTAMP,

    updated_at TIMESTAMP
);

CREATE TABLE group_members (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    group_id BIGINT NOT NULL,

    joined_at TIMESTAMP,

    created_at TIMESTAMP,

    updated_at TIMESTAMP,

    CONSTRAINT fk_group_member_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_group_member_group
        FOREIGN KEY (group_id)
        REFERENCES groups(id)
);

CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,

    amount NUMERIC(19,2) NOT NULL,

    paid_by_user_id BIGINT NOT NULL,

    group_id BIGINT NOT NULL,

    split_type VARCHAR(50) NOT NULL,

    description VARCHAR(500),

    created_at TIMESTAMP,

    updated_at TIMESTAMP,

    CONSTRAINT fk_expense_user
        FOREIGN KEY (paid_by_user_id)
        REFERENCES users(id),

    CONSTRAINT fk_expense_group
        FOREIGN KEY (group_id)
        REFERENCES groups(id)
);

CREATE TABLE expense_splits (
    id BIGSERIAL PRIMARY KEY,

    expense_id BIGINT NOT NULL,

    user_id BIGINT NOT NULL,

    share_amount NUMERIC(19,2) NOT NULL,

    created_at TIMESTAMP,

    updated_at TIMESTAMP,

    CONSTRAINT fk_split_expense
        FOREIGN KEY (expense_id)
        REFERENCES expenses(id),

    CONSTRAINT fk_split_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);