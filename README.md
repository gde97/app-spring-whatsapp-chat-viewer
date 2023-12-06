# WHATSAPP CHAT VIEWER - BACKEND 

## PART 1 OF THIS APP

Made with Spring boot framework.

It includes OPENAPI/SWAGGER.

Developed with Intellij Ultimate and Maven.

## Description

This app is for the need to save all old chat exported from whatsapp
in a database to allow apps to retrieve all data much easier than a txt file.

It's possible to have:
- different users
- infinite chat for user with different names
- infinite messages for a chat
- know if is present the attachment and its name

### To do in Spring
- manage new message from the same chat

## Requirements

|             | I used this version          |
| ----------- | ---------------------------- |
| Spring boot | 3.1.5 (installed with Maven) |
| PostgreSQL  | a version                    |

With Maven gets these dependencies: Spring Data JPA, PostgreSQL, Spring Web, Jakarta, Junit, Spring OpenAPI.
 
## Usage

### Before everything

In the folder /config there is a .property file.

Set the URL for database, user and password.

Before running its necessary that database exists,
all the table will be created automatically.

### Run from IDE

Go to the class file "AppSpringApplication" then click on the green arrow on left.

If you have Intellij Ultimate there is a run config.

### Build

Use maven to build the package.

### RUN from a jar

Need to have in the same dir:

|          | description                             |
|----------|-----------------------------------------|
| config   | folder with file application.properties |
| file.jar | executable                              |

```bash
java -jar nameOfJar.jar
```

It connects to the database.

Generate in the same folder the dir named 'all-chats-attachments' 
with all file imported.
This folder is used to keep all attachment uploaded.