%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "8010").
context(ctxthermometer, "192.168.1.100",  "TCP", "8001").
context(ctxfan, "192.168.1.100",  "TCP", "8002").
context(ctxsonar, "192.168.1.100",  "TCP", "8003").
context(ctxweightsensor, "192.168.1.100",  "TCP", "8004").
 qactor( fanactor, ctxfan, "external").
  qactor( parkingmanagerservice, ctxcarparking, "it.unibo.parkingmanagerservice.Parkingmanagerservice").
  qactor( itocccounter, ctxcarparking, "it.unibo.itocccounter.Itocccounter").
  qactor( dtfreecounter, ctxcarparking, "it.unibo.dtfreecounter.Dtfreecounter").
  qactor( parkingservicestatusgui, ctxcarparking, "it.unibo.parkingservicestatusgui.Parkingservicestatusgui").
  qactor( notificationactor, ctxcarparking, "it.unibo.notificationactor.Notificationactor").
  qactor( antifireactor, ctxcarparking, "it.unibo.antifireactor.Antifireactor").
