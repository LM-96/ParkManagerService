%====================================================================================
% basicthermometer description   
%====================================================================================
context(ctxthermometer, "localhost",  "TCP", "8001").
 qactor( thermometeractor, ctxthermometer, "it.unibo.thermometeractor.Thermometeractor").
