package gs.protocol.galileoskyprotocolservice.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for setup tcp server from env.
 */
@Configuration
@ConfigurationProperties(prefix = "tcp.server")
public class TcpServerProps {
    private Integer port;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
