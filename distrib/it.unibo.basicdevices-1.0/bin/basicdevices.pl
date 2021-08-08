%====================================================================================
% basicdevices description   
%====================================================================================
context(ctxthermometer, "localhost",  "TCP", "8001").
context(ctxfan, "localhost",  "TCP", "8002").
context(ctxsonar, "localhost",  "TCP", "8003").
context(ctxweightsensor, "localhost",  "TCP", "8004").
context(ctxcarparking, "127.0.0.1",  "TCP", "8010").
 qactor( thermometeractor, ctxthermometer, "it.unibo.thermometeractor.Thermometeractor").
  qactor( fanactor, ctxfan, "it.unibo.fanactor.Fanactor").
  qactor( sonaractor, ctxsonar, "it.unibo.sonaractor.Sonaractor").
  qactor( weightsensoractor, ctxweightsensor, "it.unibo.weightsensoractor.Weightsensoractor").
