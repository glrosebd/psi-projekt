-- Utworzenie uzytkownika.
CREATE USER mgr_psi IDENTIFIED BY 12345;

GRANT DBA TO mgr_psi;

-- Tabela dla uzytkownikow.
CREATE TABLE psi_user
(
ID		   		NUMBER NOT NULL,
login 	   		VARCHAR2(255) NOT NULL,
password   		VARCHAR2(255) NOT NULL,
email	   		VARCHAR2(255) NOT NULL,
login_fail 		NUMBER DEFAULT 0,
last_login_type DATE,
acc_block  		VARCHAR2(1) NOT NULL
);

-- Primary Key oraz sekwencja.
ALTER TABLE psi_user ADD CONSTRAINT psi_pk_id PRIMARY KEY (ID);

CREATE SEQUENCE USER_SEQUENCE
INCREMENT BY 1
START WITH 1
MAXVALUE 999
MINVALUE 1;

-- Tabela "Kalendarz" dla uzytkownikow.
CREATE TABLE psi_text
(
ID       NUMBER,
UserID   NUMBER,
usr_txt  VARCHAR2(255),
usr_date DATE
);

-- Dodanie PK i FK do powyzszej tabeli
ALTER TABLE psi_text ADD CONSTRAINT psi_text_pk PRIMARY KEY (ID);
ALTER TABLE psi_text ADD CONSTRAINT psi_text_fk FOREIGN KEY (UserID) REFERENCES psi_user(ID);

CREATE SEQUENCE TEXT_SEQUENCE
INCREMENT BY 1
START WITH 1
MAXVALUE 999
MINVALUE 1; 

-- Tabela do logowania
CREATE TABLE psi_logs
(
userID      NUMBER,
textID      NUMBER,
info        VARCHAR2(255),
change_time DATE,
status      VARCHAR2(8)
);

-- Powiązania
ALTER TABLE psi_logs ADD CONSTRAINT logs_user_fk FOREIGN KEY (userID) REFERENCES psi_user(ID);
ALTER TABLE psi_logs ADD CONSTRAINT logs_text_fk FOREIGN KEY (textID) REFERENCES psi_text(ID);
-- Triggery do logowania
CREATE OR REPLACE TRIGGER insert_logs
AFTER INSERT ON psi_text
FOR EACH ROW
BEGIN
    INSERT INTO psi_logs VALUES (:new.userid, :new.id, :new.usr_txt, SYSDATE, 'INSERT');
END;
/
CREATE OR REPLACE TRIGGER delete_logs
AFTER DELETE ON psi_text
FOR EACH ROW
BEGIN
    INSERT INTO psi_logs(userid, textid, info, change_time, status) VALUES (:old.userid, :old.id, :old.usr_txt, SYSDATE, 'DELETE'); 
END;
/
-- Scheduler do oblokywania kont o 7:00 kazdego dnia.
BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
            job_name => '"MGR_PSI"."UNBLOCK_ACCOUNT"',
            job_type => 'PLSQL_BLOCK',
            job_action => 'BEGIN
UPDATE psi_user
SET acc_block = ''N'', login_fail = 0;
COMMIT;
END;',
            number_of_arguments => 0,
            start_date => TO_TIMESTAMP_TZ('2018-02-01 07:00:00.000000000 EUROPE/WARSAW','YYYY-MM-DD HH24:MI:SS.FF TZR'),
            repeat_interval => 'FREQ=DAILY',
            end_date => NULL,
            enabled => FALSE,
            auto_drop => FALSE,
            comments => 'Odblokowywanie kont.');

         
     
 
    DBMS_SCHEDULER.SET_ATTRIBUTE( 
             name => '"MGR_PSI"."UNBLOCK_ACCOUNT"', 
             attribute => 'logging_level', value => DBMS_SCHEDULER.LOGGING_OFF);
      
  
    
    DBMS_SCHEDULER.enable(
             name => '"MGR_PSI"."UNBLOCK_ACCOUNT"');
END;
/
