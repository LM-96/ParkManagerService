echo off
echo "Starting server..."
start cmd /k "cd it.unibo.webServer/src & python manage.py runserver"
sleep 5
echo "Starting basicrobot..."
start cmd /k "cd distrib/it.unibo.qak21.basicrobot-1.0/bin & bash it.unibo.qak21.basicrobot"
echo "Starting basicdevices..."
start cmd /k "cd distrib/it.unibo.basicdevices-1.0/bin & bash it.unibo.basicdevices"
echo "Starting parkmanagerservice..."
start cmd /k "cd distrib/it.unibo.parkmanagerservice-1.0/bin & bash it.unibo.parkmanagerservice"
echo "Starting KCProxy..."
start cmd /k "cd distrib/KCProxy-1.0/bin & bash KCProxy-1.0"
echo "All started"