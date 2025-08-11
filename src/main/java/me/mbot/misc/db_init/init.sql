-- creates tables with user_id (discord) and xp (amount of xp a disc user has)
CREATE TABLE user_xp (
    user_id BIGINT PRIMARY KEY,
    xp INT NOT NULL DEFAULT 0
);

CREATE TABLE message_edit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    old_content TEXT,
    new_content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE message_delete_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    channel_id VARCHAR(50) NOT NULL,
    content TEXT,
    image_urls TEXT,
    video_urls TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS spam_tracker (
    user_id TEXT NOT NULL,
    timestamp_ms BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS user_todos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    todo TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reminders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    remind_at TIMESTAMP NOT NULL,
    note TEXT NOT NULL,
    channel_id VARCHAR(50) NOT NULL
);

-- auto delete old edit logs
CREATE EVENT IF NOT EXISTS delete_old_logs_edit
ON SCHEDULE EVERY 1 DAY
DO
DELETE FROM message_edit_logs WHERE created_at < NOW() - INTERVAL 30 DAY;

-- auto delete old delete logs
CREATE EVENT IF NOT EXISTS delete_old_logs_delete
ON SCHEDULE EVERY 1 DAY
DO
DELETE FROM message_delete_logs WHERE created_at < NOW() - INTERVAL 30 DAY;

CREATE EVENT IF NOT EXISTS cleanup_spam_tracker
ON SCHEDULE EVERY 5 MINUTE
DO
DELETE FROM spam_tracker WHERE timestamp_ms < (UNIX_TIMESTAMP(NOW(3)) * 1000) - 60000;
