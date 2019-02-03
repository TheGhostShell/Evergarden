INSERT INTO evergarden_user (email, firstname, lastname, password, salt, activated)
VALUES ( 'violet@mail.com', 'Violet', 'Evergarden',  'password', 'salt', true );

INSERT INTO EVERGARDEN_ROLE (ROLE)
VALUES ( 'ROLE_MASTER_ADMIN' );

SET @idRole = (SELECT  ID FROM EVERGARDEN_ROLE WHERE ROLE = 'ROLE_MASTER_ADMIN');
SET @idUser = (SELECT  ID FROM EVERGARDEN_USER WHERE EMAIL = 'violet@mail.com');


INSERT INTO EVERGARDEN_USER_ROLES (USER_ID, ROLE_ID)
VALUES ( @idUser, @idRole );