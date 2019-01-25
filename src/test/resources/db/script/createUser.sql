INSERT INTO evergarden_user (email, firstname, lastname, pseudo, password, salt, activated)
VALUES ( 'batou@mail.com', 'batou', 'ranger', 'batou', 'password', 'salt', true );

INSERT INTO EVERGARDEN_ROLE (ROLE)
VALUES ( 'ROLE_TEST_ADMIN' );

SET @idRole = (SELECT  ID FROM EVERGARDEN_ROLE WHERE ROLE = 'ROLE_TEST_ADMIN');
SET @idUser = (SELECT  ID FROM EVERGARDEN_USER WHERE EMAIL = 'batou@mail.com');


INSERT INTO EVERGARDEN_USER_ROLES (USER_ID, ROLE_ID)
VALUES ( @idUser, @idRole );