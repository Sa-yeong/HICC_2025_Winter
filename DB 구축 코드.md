# 구축한 DB
## 결과 사진
<img width="1279" height="1200" alt="image" src="https://github.com/user-attachments/assets/0aef69a3-fea2-4ae4-8b09-d768304cb5cd" />

## 1. Users 테이블
```sql
Create Table users(
	id BigInt Not NULL Auto_Increment,
	login_id varchar(50) NOT NULL Unique,
	password varchar(255) NOT NULL,
	birth_date date NOT NULL,
	gender varchar(10) NOT NULL, 
	nickname varchar(20) NOT NULL Unique,
	Primary Key (id)
);
```

## 2. Posts 테이블
```sql
Create Table posts (
	id BigInt Not NULL Auto_Increment,
	writer_id BigInt NOT NULL,
	title Varchar(100) NOT NULL,
	content Text Not NULL,
	Genre VarChar(10) Not Null,
	state Boolean Default True,
	mate_preference Varchar(100),
	performance_name VarChar(100) Not NULL,
	performance_date DateTime Not NULL,
	performance_location Varchar(100) Not Null,
	create_time DateTime Default CURRENT_TIMESTAMP(),
	Primary Key (id),
	Foreign Key (writer_id) references users(id) On Delete CASCADE
);
```

## 3. Chats 테이블
```sql
Create Table chats(
	id BigInt Not NULL Auto_Increment,
	post_id BigInt NOT NULL,
	create_date DateTime default Current_timestamp,
	Primary Key (id),
	Foreign Key (post_id) References posts(id) On DELETE CASCADE
);
```

## 4. Scrap 테이블
``` sql
Create Table scrap (
	id BigINT NOT NULL Auto_Increment,
	user_id BigINT NOT NULL,
	post_id BigInt NOT NULL,
	Primary key (id),
	Foreign Key (user_id) References users(id) ON DELETE CASCADE,
	Foreign Key (post_id) References posts(id) ON DELETE CASCADE,
	Unique(user_id, post_id)
);
```

## 5. Preferences 테이블
```sql
Create Table preferences(
	id BigInt NOT NULL Auto_Increment,
	user_id BigInt NOT NULL,
	preference Varchar(30) NOT NULL,
	Primary Key (id),
	Foreign Key (user_id) References users(id) ON DELETE CASCADE,
	Unique(user_id, preference)
);
```

## 6. Participate 테이블
```sql
Create Table participate (
	id BigInt NOT NULL Auto_Increment,
	chat_id BigInt NOT NULL,
	member_id BigInt NOT NULL,
	Primary Key(id),
	Foreign Key (chat_id) references chats(id) ON DELETE CASCADE,
	Foreign Key (member_id) references users(id) ON DELETE CASCADE,
	Unique(chat_id, member_id)
);
```

## 7. Message 테이블
```sql
Create Table messages (
	id BigInt NOT NULL Auto_Increment,
	sender_id BigInt,
	chat_id BigInt NOT NULL,
	content Text NOT NULL,
	create_time dateTime Default CURRENT_TIMESTAMP,
	Primary Key(id),
	Foreign Key(sender_id) References users(id) ON DELETE SET NULL,
	Foreign Key(chat_id) References chats(id) ON DELETE CASCADE
);
```
