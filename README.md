# server needs to run before client

# config file path
framework.view.fileManagerPage: line 430, split("\\"+File.separator)

# First week
backend: login/signup/changeAccountSettings/logout
{
    method: run and debug
    file: {
        src/server/tests/ServerMain.java, 
        src/client/tests/ClientMain.java
    }
}
frontend: login/signup page
{
    method: run and debug
    file: {
        src/server/tests/ServerMain.java, 
        src/framework/view/fileStorage.java
    }
}

# Second week:
missions:{
    create/update/delete file/folder,
    upload file/folder,
    share files
}
run and debug: {
    src/server/tests/ServerMain.java,
    src/framework/view/fileStorage.java
}

# Third week:
mission: file concurrent editing
