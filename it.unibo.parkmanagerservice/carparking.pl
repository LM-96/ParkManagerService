%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "8010").
context(ctxthermometer, "127.0.0.1",  "TCP", "8001").
context(ctxfan, "127.0.0.1",  "TCP", "8002").
context(ctxsonar, "127.0.0.1",  "TCP", "8003").
context(ctxweightsensor, "127.0.0.1",  "TCP", "8004").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( fanactor, ctxfan, "external").
  qactor( weightsensoractor, ctxweightsensor, "external").
  qactor( sonaractor, ctxsonar, "external").
  qactor( basicrobot, ctxbasicrobot, "external").
  qactor( parkingmanagerservice, ctxcarparking, "it.unibo.parkingmanagerservice.Parkingmanagerservice").
  qactor( itocccounter, ctxcarparking, "it.unibo.itocccounter.Itocccounter").
  qactor( dtfreecounter, ctxcarparking, "it.unibo.dtfreecounter.Dtfreecounter").
  qactor( parkingservicestatusgui, ctxcarparking, "it.unibo.parkingservicestatusgui.Parkingservicestatusgui").
  qactor( notificationactor, ctxcarparking, "it.unibo.notificationactor.Notificationactor").
  qactor( antifireactor, ctxcarparking, "it.unibo.antifireactor.Antifireactor").
  qactor( trolley, ctxcarparking, "it.unibo.trolley.Trolley").
