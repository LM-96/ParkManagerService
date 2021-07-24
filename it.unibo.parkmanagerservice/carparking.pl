%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "8010").
  qactor( parkingmanagerservice, ctxcarparking, "it.unibo.parkingmanagerservice.Parkingmanagerservice").
  qactor( itocccounter, ctxcarparking, "it.unibo.itocccounter.Itocccounter").
  qactor( dtfreecounter, ctxcarparking, "it.unibo.dtfreecounter.Dtfreecounter").
  qactor( parkingservicestatusgui, ctxcarparking, "it.unibo.parkingservicestatusgui.Parkingservicestatusgui").
