-----------------------

UPDATE PSI_USER SET Login_Fail = 2, Last_Login_Type = SYSDATE WHERE Login = 'Krzychu'

CREATE USER mgr_psi IDENTIFIED BY 12345;

GRANT DBA TO mgr_psi;

-- Na powy¿szym u¿ytkowniku:
CREATE TABLE psi_user
(
ID		   		NUMBER NOT NULL,
login 	   		VARCHAR2(255) NOT NULL,
password   		VARCHAR2(255) NOT NULL,
email	   		VARCHAR2(255) NOT NULL,
login_fail 		NUMBER DEFAULT 0,
last_login_type DATE,
acc_block  		VARCHAR2(1) NOT NULL,
);

ALTER TABLE psi_user ADD CONSTRAINT psi_pk_id PRIMARY KEY (ID);

CREATE SEQUENCE USER_SEQUENCE
INCREMENT BY 1
START WITH 1
MAXVALUE 999
MINVALUE 1;

--bledny trig.

CREATE OR REPLACE TRIGGER block_acc
BEFORE UPDATE ON psi_user
FOR EACH ROW
BEGIN
    UPDATE psi_user
    SET acc_block = 'Y'
    WHERE login_fail >= 5;
END;