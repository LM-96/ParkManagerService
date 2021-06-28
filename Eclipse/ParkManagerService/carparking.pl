%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "8000").
 qactor( parkingservicegui, ctxcarparking, "it.unibo.parkingservicegui.Parkingservicegui").
  qactor( parkingmanagerservice, ctxcarparking, "it.unibo.parkingmanagerservice.Parkingmanagerservice").
