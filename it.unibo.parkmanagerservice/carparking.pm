%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "8010").
  qactor( parkingmanagerservice, ctxcarparking, "it.unibo.parkingmanagerservice.Parkingmanagerservice").
  qactor( itocccounter, ctxcarparking, "it.unibo.itocccounter.Itocccounter").
  qactor( parkingmanagerservice, ctxcarparking, "it.unibo.parkingmanagerservice.Parkingmanagerservice").
  qactor( dtfreecounter, ctxcarparking, "it.unibo.dtfreecounter.Dtfreecounter").
  qactor( notificationactor, ctxcarparking, "it.unibo.notificationactor.Notificationactor").
  qactor( antifireactor, ctxcarparking, "it.unibo.antifireactor.Antifireactor").
