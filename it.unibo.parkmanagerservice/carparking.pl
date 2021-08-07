%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "8010").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
  qactor( trolley, ctxcarparking, "it.unibo.trolley.Trolley").
  qactor( basicrobot, ctxbasicrobot, "external").
