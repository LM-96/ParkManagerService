echo off
echo "Please check you have started the Virtual Robot at port 8090 if QAK system are configured to use it.
pause
echo "Starting server..."
start cmd /k "cd it.unibo.webServer/src & ..\..\..\werServerTest\.venv\Scripts\activate & python manage.py runserver"
echo "When the server is started press enter to start the QAK systems"
pause
echo "Starting basicrobot..."
start cmd /k "cd distrib/it.unibo.qak21.basicrobot-1.0/bin & bash it.unibo.qak21.basicrobot"
echo "Starting basicdevices..."
start cmd /k "cd distrib/it.unibo.basicdevices-1.0/bin & bash it.unibo.basicdevices"
echo "Starting parkmanagerservice..."
start cmd /k "cd distrib/it.unibo.parkmanagerservice-1.0/bin & bash it.unibo.parkmanagerservice"
echo "When the QAK systems are started press enter to start the proxy"
pause
echo "Starting KCProxy..."
start cmd /k "cd distrib/KCProxy-1.0/bin & bash KCProxy-1.0"
echo "All started"