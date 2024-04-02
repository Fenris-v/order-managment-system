INSERT INTO users (email, password, created_at, updated_at)
VALUES ('test-user@mail.ru',
        '$argon2id$v=19$m=4096,t=3,p=1$jwWXCa11R24TSwshD2/Ihg$SFrQVhSQgxLpl7K76bJBsRtDhHRHyWvqLtyBebbTIDo',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
