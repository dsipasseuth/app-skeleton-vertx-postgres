CREATE TABLE IF NOT EXISTS bows (
    id SERIAL PRIMARY KEY,
    bow_type VARCHAR(50) NOT NULL,
    draw_weight INT NOT NULL,
    draw_length INT NOT NULL
);
