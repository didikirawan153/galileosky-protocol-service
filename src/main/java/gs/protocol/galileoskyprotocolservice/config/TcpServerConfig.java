package gs.protocol.galileoskyprotocolservice.config;

import gs.protocol.galileoskyprotocolservice.config.props.TcpServerProps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.messaging.MessageChannel;

/**
 * Tcp server configuration when start the program.
 */
@Configuration
@Slf4j
public class TcpServerConfig {
    private final TcpServerProps tcpServerProps;

    public TcpServerConfig(TcpServerProps tcpServerProps) {
        this.tcpServerProps = tcpServerProps;
    }

    /**
     * Define Connection Factory for server.
     * This bean is responsible for opening port server and listening for incoming connections.
     * TcpNioServerConnectionFactory uses Java NIO which is more scalable.
     */
    @Bean
    public AbstractServerConnectionFactory serverConnectionFactory() {
        TcpNioServerConnectionFactory connectionFactory =
            new TcpNioServerConnectionFactory(tcpServerProps.getPort());
        log.info("TCP/IP protocol running with port : {}", tcpServerProps.getPort());
        return connectionFactory;
    }

    /**
     * Defines the input channel where messages from TCP will be placed.
     */
    @Bean
    public MessageChannel tcpInboundChannel() {
        return new DirectChannel();
    }

    /**
     * Defines the Inbound Gateway. This gateway is connected to the Connection Factory and listens
     * for incoming data. Received data will be sent as a message to ‘requestChannel’
     * (tcpInboundChannel). Responses from the service activator will be sent back to the client
     * through the same connection.
     */
    @Bean
    public TcpInboundGateway tcpInboundGateway(
        AbstractServerConnectionFactory serverConnectionFactory) {
        TcpInboundGateway gateway = new TcpInboundGateway();
        gateway.setConnectionFactory(serverConnectionFactory);
        gateway.setRequestChannel(tcpInboundChannel());
        return gateway;
    }
}
