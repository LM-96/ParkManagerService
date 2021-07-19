%====================================================================================
% basicdevice description   
%====================================================================================
context(ctxsonar, "localhost",  "TCP", "8003").
 qactor( sonaractor, ctxsonar, "it.unibo.sonaractor.Sonaractor").
