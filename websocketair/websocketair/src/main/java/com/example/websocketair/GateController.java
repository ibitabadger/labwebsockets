package com.example.websocketair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/gates")
@CrossOrigin(origins = "http://localhost:3000")
public class GateController {
   @Autowired
    private SimpMessagingTemplate messagingTemplate;

   public GateController(SimpMessagingTemplate messagingTemplate){
       this.messagingTemplate = messagingTemplate;
   }

   //Recibir informacion desde el cliente (REACT) y enviar a todos los subscriptores

    @MessageMapping("/updateGate")
    @SendTo("/topic/gates")
    public GateInfo updateGateInfo(GateInfo gateInfo) throws Exception{
       System.out.println("Actualizando informacion de la puerta: "+ gateInfo.getGate());
       messagingTemplate.convertAndSend("/topic/gates", gateInfo);
       //Devolver la informacion de la puerta a todos los subscriptores
        return new GateInfo(
                HtmlUtils.htmlEscape(gateInfo.getGate()),
                HtmlUtils.htmlEscape(gateInfo.getFlightNumber()),
                HtmlUtils.htmlEscape(gateInfo.getDestination()),
                HtmlUtils.htmlEscape(gateInfo.getDepartureTime()),
                HtmlUtils.htmlEscape(gateInfo.getStatus())
        );
   }
   
   
   //Metodo para enviar actualizaciones programaticas o desde un servicio externo
    public void sendUpdate(GateInfo gateInfo){
       //Enviar los datos actualizados de una puerta de embarque a todos los subscriptores en /topic/gates
       messagingTemplate.convertAndSend("/topic/gates", gateInfo); 
   }
   
   private Map<String, GateInfo> gateData = new ConcurrentHashMap<>();
   //Metodos para actualizar la información de la puerta de embarque
    @PostMapping("/update")
    public ResponseEntity<String> updateGate(@RequestBody GateInfo gate) {
        //actualiza los datos de la puerta de embarque
        gateData.put(gate.getGate(), gate);
        //enviar la actualizacion a todos los subc del websocket
        messagingTemplate.convertAndSend("/topic/gates", gate);
        
        return ResponseEntity.ok("Puerta de embarque ha sido actualizada con exito");
        
    }
    
    @GetMapping("/{gateNumber}")
    public ResponseEntity<GateInfo> getGateInfo(@PathVariable String gateNumber) {
        GateInfo gate = gateData.get(gateNumber);
        return ResponseEntity.ok(gate);
    }
    
}
