create database infocom;
use infocom;

-- Use SQL to enter in the first 5 values for each table

-- Roles
create role 'Admin', 'SE', 'HR', 'PR', 'General';
create user admin;
grant Admin to admin;


-- Only admin can access this table
grant all on LogIn_Credential to Admin;
create table LogIn_Credential (
	username varchar(50),
    password varchar(50),
    role varchar(7) -- 7 because 'General' is the longest role

);

-- insert into LogIn_Credential
insert into LogIn_Credential values ('adminUser', 'adminPass', 'Admin');
insert into LogIn_Credential values ('SEUser', 'SEPass', 'SE');
insert into LogIn_Credential values ('HRUser', 'HRPass', 'HR');
insert into LogIn_Credential values ('PRUser', 'PRPass', 'PR');
insert into LogIn_Credential values ('generalUser', 'generalPass', 'General');
select * from LogIn_Credential;
drop table LogIn_Credential;

-- SE and Admin can access this table
create table SE_Data (
	id char (5),
    name varchar (50),
    projectname varchar (50),
    supervisor varchar (50),
    deadline varchar (10), -- This is a date
    primary key (id)
);

-- insert into SE_Data
insert into SE_Data values ('12345', 'Gary', 'C+++', 'Soup', '10/10/2010');
insert into SE_Data values ('15951', 'Yonker', 'C+++', 'Soup', '10/10/2010');
insert into SE_Data values ('10110', 'Frank', 'Avaj', 'BossMan', '09/27/2058');
insert into SE_Data values ('99999', 'Grant', 'Avaj', 'SuperBoss', '09/27/2058');
insert into SE_Data values ('11111', 'Avery', 'Avaj', 'SuperBoss', '02/05/2050');
select * from SE_Data;
drop table SE_Data;

-- SE, HR, PR, General and Admin can access this table
create table Emp_SE (
	id char (5),
    address varchar (120),
    phone_no varchar (11), -- Will scrap any non-numerical data before adding
    salary numeric (8, 2),
    bloodgroup varchar (3), -- A-, A+, AB+, so on
	primary key (id)
);

-- insert into Emp_SE
insert into Emp_SE values ('12345', '20 Timber Ave', '1230984567', 80000.00, 'A-');
insert into Emp_SE values ('15951', '1234 Great St', '1213435656', 100000.00, 'B+');
insert into Emp_SE values ('10110', '5050 Boolean Way', '1010010010', 101100.10, 'AB-');
insert into Emp_SE values ('99999', '999 HighNumber Ln', '9999999999', 99999.99, 'AB+');
insert into Emp_SE values ('11111', '1 LowNumber Blvd', '1111111111', 111111.11, 'O');
select * from Emp_SE;
drop table Emp_SE;

-- HR and Admin can access this table
create table HR_data (
	id char (5),
    name varchar (50),
    department varchar (50),
    supervisor varchar (50),
    primary key (id)
);

-- insert into HR_Data
insert into HR_Data values ('59595', 'Joe', 'dept1', 'BossMan');
insert into HR_Data values ('98789', 'Jarid', 'dept1', 'Soup');
insert into HR_Data values ('24680', 'Destin', 'dept2', 'Soup');
insert into HR_Data values ('10001', 'Terry', 'dept2', 'Soup');
insert into HR_Data values ('12321', 'Matt', 'dept2', 'SuperBoss');
select * from HR_Data;
drop table HR_Data;

-- HR, PR, General and Admin can access this table
create table Emp_HR (
	id char (5),
    address varchar (120),
    phone_no varchar (11),
    ranking varchar (20), -- 'rank' already exists in sql
	primary key (id)
);

-- insert into Emp_HR
insert into Emp_HR values ('59595', '595 Road Rd', '5955955995', 'rank1');
insert into Emp_HR values ('98789', '987 Avenue Ave', '9879879876', 'rank2');
insert into Emp_HR values ('24680', '2468 Lane Ln', '1352463579', 'rank2');
insert into Emp_HR values ('10001', '1 Street St', '1011011001', 'rank2');
insert into Emp_HR values ('12321', 'Boulevard Blvd', '1231231232', 'rank3');
select * from Emp_HR;
drop table Emp_HR;

-- HR, PR, General and Admin can access this table
create table Emp_PR (
	id char (5),
    address varchar (120),
    phone_no varchar (11),
    salary numeric (8, 2),
    primary key (id)
);

-- insert into Emp_PR
insert into Emp_PR values ('99887', '987 Grand St', '9998887777', '98798.70');
insert into Emp_PR values ('88776', '876 Brently Ln', '8887776666', '87687.60');
insert into Emp_PR values ('77665', '765 Skyler Rd', '7776665555', '76576.50');
insert into Emp_PR values ('66554', '654 Pinkman Blvd', '6665554444', '65465.40');
insert into Emp_PR values ('55443', '543 White Ave', '5554443333', '54354.30');
select * from Emp_PR;
drop table Emp_PR;

-- PR and Admin can access this table
create table PR_Data (
	id char (5),
    firstname varchar (50),
    lastname varchar (50),
    dob varchar (10), -- This is a date
	primary key (id)
);

-- insert into PR_Data
insert into PR_Data values ('99887', 'Nick', 'Grand', '05/04/2001');
insert into PR_Data values ('88776', 'Brent', 'Brently', '09/22/1989');
insert into PR_Data values ('77665', 'Taylor', 'Skyler', '08/19/1991');
insert into PR_Data values ('66554', 'Walter', 'Pinkman', '03/22/1960');
insert into PR_Data values ('55443', 'Jesse', 'White', '06/04/2002');
select * from PR_Data;
drop table PR_Data;
