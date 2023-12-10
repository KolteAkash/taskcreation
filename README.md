# Task Manager

#### Steps to Run Locally

**Initial Steps**
1. Clone the repository: `git clone <repository_url>`
2. Change to the project directory: `cd task_management_back_end`
3. List all branches: `git branch -a`
4. Checkout to the desired branch: `git checkout <branch_name>`

**Changes in application properties file**

Update the following properties in the `application.properties` file:


Make sure to replace `<database_name>`, `<database_username>`, `<database_password>`, and `<secret_string>` with the appropriate values.

These changes will configure the database connection and other settings for the application.

By following these steps and making the necessary changes, you should be able to run the Task Manager application locally.

## How to Use Application as Superadmin

The application allows Superadmins to manage clients. To add a client, you can follow these steps:

1. Ensure that the application is running locally on `http://localhost:8080/api/v1/auth/login`. make post request
 In the request body, provide the following JSON payload:
```json
{
    "email":"superadmin@taskmanager.com",
    "password":"in main file"
}
```
if you get response 
```json
{
    "headers": {},
    "body": {
        "token": "you get something token here",
        "refreshToken": "you get something token here",
        "userName": "superAdmin",
        "email": "superadmin@taskmanager.com"
    },
    "statusCode": "OK",
    "statusCodeValue": 200
}
```
**Then Move forward**
We have two clients and their respective admins:

- **FTBCOLAB**
 - Admin: kedar@ftbcolab.com

- **FTBCommunication**
 - Admin: moin@ftbcommunication.com<br>
*These clients and admins are already defined in the code for testing purposes we have to set client id in it*
2. Use a tool like Postman or cURL to send a POST request to `http://localhost:8080/client/create-client`. to declare client
for this request we need super admin token set it into Bearer token 
In the request body, provide the following JSON payload:

```json
{
 "client_name":"Ftb Colab",
 "client_details":"Software Company",
 "client_admin":"kedar@ftbcolab.com"
}
```
**For Verify you can see client table in database**<br>
3. Now add users to client Only Client Admin Can add users in particular Company
    this request need Admin token Not SuperAdmin
   Use a tool like Postman or cURL to send a POST request to`http://localhost:8080/admincontroll/create_user`;
   In the request body, provide the following JSON payload:
```JSON
{
 "user_name":"Omkar K",
 "email":"omkark@ftbcolab.com",
 "password":"pass",
 "mobile_number":"9879879877"
} 
```
**For Verify you can see client_user table in database**<br>
***Now Add Projects Only Admin Can Add Projects (Admin Token)***
4. cURL to send a POST request to `http://localhost:8080/projects/create-project`
<p>In the request body, provide the following JSON payload:</p>

```JSON
{
    "projectKey":"Navis",//Unique
    "projectName":"Web app",
    "projectType":"Web Development"
   
}
```
**For Verify you can see project table in database**<br>
5. Now Assign Project to Your Dev<br>
   cURL to send a POST request to `http://localhost:8080/projects/relation-user-project`
<p>In the request body, provide the following JSON payload:</p>

```JSON
{
    "projectId":4,//Project id should present database
    "userName":"<user mail>"
}
```
6. Refresh Token Request In case Access Token Expire<br>
   cURL to send a POST request to `http://localhost:8080/api/v1/auth/refresh`
```JSON
{
    "refreshToken":"<Refresh Token From Login Response>"
}
```
## Important Get Request
### Get all client List (Super admin Request)
curl -X GET http://localhost:8080/client

### Get all assigned Projects (User, Admin Request)
curl -X GET http://localhost:8080/projects

### Get a Single Project (User, Admin Request)
curl -X GET http://localhost:8080/projects/{id}
### Replace {id} with the actual ID of the project

### Demo Request (User, Admin, SuperAdmin Request)
curl -X GET http://localhost:8080/validator/checkList

### Get all Users and Projects (Admin Request)
curl -X GET http://localhost:8080/projects/passing-list

### Get all client users (Admin Request)
curl -X GET http://localhost:8080/admincontroll

