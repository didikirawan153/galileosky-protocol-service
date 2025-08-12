package gs.protocol.galileoskyprotocolservice.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.tcp.connection.TcpConnectionCloseEvent;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

/**
 * Service for processing message from TCP client.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TcpProcessingService {
    // For caching imei conectionId.
    private final Map<String, String> clientImeiMap = new ConcurrentHashMap<>();

    /**
     * This method will be automatically called when there is a message in “tcpInboundChannel”.
     * Spring Integration is smart enough to extract the payload from the message (in this case
     * byte[]). The String value returned by this method will become the payload of the response
     * message.
     *
     * @param message Incoming message in the form of a byte array.
     * @return Response message in the form of a String.
     */
    @ServiceActivator(inputChannel = "tcpInboundChannel")
    public byte[] processMessage(Message<byte[]> message) {
        // Extract connectionId from header.
        String connectionId = (String) message.getHeaders().get(IpHeaders.CONNECTION_ID);

        String payload = new String(message.getPayload()).trim();
        log.debug("Received data packet : {} With connectionId : {}", payload, connectionId);

        // Todo: will be change with ack standard GS Protocol.
        byte[] ackMessage = payload.getBytes();
        // Todo: Logic for filter head packet.
        if (!clientImeiMap.containsKey(connectionId)) {
            // Todo : Handle method/service for decode head packet.

            String imei = "IMEI";
            clientImeiMap.put(connectionId, imei);
        } else {
            String imei = clientImeiMap.get(connectionId);
            // Todo : Handle method/service for decode main packet.
        }

        return ackMessage;
    }

    /**
     * Event Listener for handle when connection is closed.
     * This is important to avoid memory leak.
     */
    @EventListener
    public void handleConnectionClose(TcpConnectionCloseEvent event) {
        String connectionId = event.getConnectionId();
        String imei = clientImeiMap.remove(connectionId);
        if (imei != null) {
            log.info("TCP Connection closed. Deleted cache for IMEI: {} (connectionId: {})", imei,
                connectionId);
        } else {
            log.info("TCP connection closed : {}", connectionId);
        }
    }
}
