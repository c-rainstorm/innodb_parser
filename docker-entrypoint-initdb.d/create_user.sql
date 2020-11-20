create user traceless identified by 'traceless';

grant all privileges on *.* to traceless@'%' identified by 'traceless';

flush privileges;