--KAUF
INSERT INTO TRADEJOB(TRADEJOB_PK, CRYPTOWAEHRUNG , CRYPTOWAEHRUNG_REFERENZ, ERSTELLTAM , ERLEDIGTAM, JOB_STATUS, MENGE, KAUFWERT, ZIELWERT, LETZTWERT, TRADE_STATUS, PLATTFORM)
VALUES (101, 'LTC', 'BTC', SYSTIMESTAMP, NULL, 'K', 0.001, 0.005, 0.004, 0.006, 'ERSTELLT', 'BIC');
INSERT INTO TRADEJOB(TRADEJOB_PK, CRYPTOWAEHRUNG , CRYPTOWAEHRUNG_REFERENZ, ERSTELLTAM , ERLEDIGTAM, JOB_STATUS, MENGE, KAUFWERT, ZIELWERT, LETZTWERT, TRADE_STATUS, PLATTFORM)
VALUES (102, 'XVG', 'BTC', SYSTIMESTAMP , SYSTIMESTAMP, 'KE', 300.00005, 0.00005234, 0.00002234, 0.00001234, 'ERSTELLT', 'BIC');
--VERKAUF
INSERT INTO TRADEJOB(TRADEJOB_PK, CRYPTOWAEHRUNG , CRYPTOWAEHRUNG_REFERENZ, ERSTELLTAM , ERLEDIGTAM, JOB_STATUS, MENGE, KAUFWERT, ZIELWERT, LETZTWERT, TRADE_STATUS, PLATTFORM)
VALUES (501, 'LTC', 'BTC', SYSTIMESTAMP, NULL, 'V', 0.001, 0.005, 0.006, 0.004, 'ERSTELLT', 'BIC');
INSERT INTO TRADEJOB(TRADEJOB_PK, CRYPTOWAEHRUNG , CRYPTOWAEHRUNG_REFERENZ, ERSTELLTAM , ERLEDIGTAM, JOB_STATUS, MENGE, KAUFWERT, ZIELWERT, LETZTWERT, TRADE_STATUS, PLATTFORM)
VALUES (502, 'XVG', 'BTC', SYSTIMESTAMP , SYSTIMESTAMP, 'VE', 300.00005, 0.00001234, 0.00002234, 0.00003234, 'ERSTELLT', 'BIC');