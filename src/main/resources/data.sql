INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');

INSERT INTO users(username, email, password) VALUES('admin', 'admin@example.com', '$2a$10$HvpzVKWTLIfxOYDoTA2EiOGmIuh4aOPjAtqlF/OKyMymr5hxKYKQG');

INSERT INTO user_roles(user_id, role_id) SELECT u.id as user_id, r.id as role_id FROM users u, roles r;